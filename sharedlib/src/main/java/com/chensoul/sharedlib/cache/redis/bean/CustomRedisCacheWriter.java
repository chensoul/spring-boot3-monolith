package com.chensoul.sharedlib.cache.redis.bean;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.redis.cache.CacheStatistics;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link RedisCacheWriter} implementation capable of reading/writing binary data from/to
 * Redis in {@literal standalone} and {@literal cluster} environments. Works upon a given
 * {@link RedisConnectionFactory} to obtain the actual {@link RedisConnection}.
 * {@link CustomRedisCacheWriter} can be used in
 * {@link RedisCacheWriter#lockingRedisCacheWriter(RedisConnectionFactory) locking} or
 * {@link RedisCacheWriter#nonLockingRedisCacheWriter(RedisConnectionFactory) non-locking}
 * mode. While {@literal non-locking} aims for maximum performance it may result in
 * overlapping, non atomic, command execution for operations spanning multiple Redis
 * interactions like {@code putIfAbsent}. The {@literal locking} counterpart prevents
 * command overlap by setting an explicit lock key and checking against presence of this
 * key which leads to additional requests and potential command wait times.
 *
 * @author Christoph Strobl
 * @author Mark Paluch
 * @since 2.0
 */
public class CustomRedisCacheWriter implements RedisCacheWriter {
	private final RedisConnectionFactory connectionFactory;
	private final Duration sleepTime;
	private final CacheStatisticsCollector statistics;

	/**
	 * @param connectionFactory must not be {@literal null}.
	 */
	public CustomRedisCacheWriter(RedisConnectionFactory connectionFactory) {
		this(connectionFactory, Duration.ZERO);
	}

	/**
	 * @param connectionFactory must not be {@literal null}.
	 * @param sleepTime         sleep time between lock request attempts. Must not be
	 *                          {@literal null}. Use {@link Duration#ZERO} to disable locking.
	 */
	public CustomRedisCacheWriter(RedisConnectionFactory connectionFactory, Duration sleepTime) {
		this(connectionFactory, sleepTime, CacheStatisticsCollector.none());
	}

	public CustomRedisCacheWriter(RedisConnectionFactory connectionFactory, Duration sleepTime,
								  CacheStatisticsCollector cacheStatisticsCollector) {
		Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");
		Assert.notNull(sleepTime, "SleepTime must not be null!");
		Assert.notNull(cacheStatisticsCollector, "CacheStatisticsCollector must not be null!");
		this.connectionFactory = connectionFactory;
		this.sleepTime = sleepTime;
		this.statistics = cacheStatisticsCollector;
	}

	private static boolean shouldExpireWithin(@Nullable Duration ttl) {
		return ttl != null && !ttl.isZero() && !ttl.isNegative();
	}

	private static byte[] createCacheLockKey(String name) {
		return (name + "~lock").getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public void put(String name, byte[] key, byte[] value, @Nullable Duration ttl) {
		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(key, "Key must not be null!");
		Assert.notNull(value, "Value must not be null!");

		execute(name, connection -> {
			if (shouldExpireWithin(ttl)) {
				connection.set(key, value, Expiration.from(ttl.toMillis(), TimeUnit.MILLISECONDS), SetOption.upsert());
			} else {
				connection.set(key, value);
			}
			return "OK";
		});

		statistics.incPuts(name);
	}

	@Override
	public CompletableFuture<Void> store(String name, byte[] key, byte[] value, Duration ttl) {
		return null;
	}

	@Override
	public byte[] get(String name, byte[] key) {
		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(key, "Key must not be null!");
		byte[] result = execute(name, connection -> connection.get(key));

		statistics.incGets(name);

		if (result != null) {
			statistics.incHits(name);
		} else {
			statistics.incMisses(name);
		}

		return result;
	}

	@Override
	public CompletableFuture<byte[]> retrieve(String name, byte[] key, Duration ttl) {
		return null;
	}

	@Override
	public byte[] putIfAbsent(String name, byte[] key, byte[] value, @Nullable Duration ttl) {

		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(key, "Key must not be null!");
		Assert.notNull(value, "Value must not be null!");

		return execute(name, connection -> {
			if (isLockingCacheWriter()) {
				doLock(name, connection);
			}
			try {
				boolean put;
				if (shouldExpireWithin(ttl)) {
					put = connection.set(key, value, Expiration.from(ttl), SetOption.ifAbsent());
				} else {
					put = connection.setNX(key, value);
				}
				if (put) {
					statistics.incPuts(name);
					return null;
				}
				return connection.get(key);
			} finally {
				if (isLockingCacheWriter()) {
					doUnlock(name, connection);
				}
			}
		});
	}

	@Override
	public void remove(String name, byte[] key) {
		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(key, "Key must not be null!");

		execute(name, connection -> connection.del(key));
		statistics.incDeletes(name);
	}

	@Override
	public void clean(String name, byte[] pattern) {
		Assert.notNull(name, "Name must not be null!");
		Assert.notNull(pattern, "Pattern must not be null!");

		execute(name, connection -> {
			boolean wasLocked = false;

			try {
				if (isLockingCacheWriter()) {
					doLock(name, connection);
					wasLocked = true;
				}

				byte[][] keys = Optional.ofNullable(connection.keys(pattern)).orElse(Collections.emptySet())
					.toArray(new byte[0][]);

				if (keys.length > 0) {
					statistics.incDeletesBy(name, keys.length);
					connection.del(keys);
				}
			} finally {
				if (wasLocked && isLockingCacheWriter()) {
					doUnlock(name, connection);
				}
			}

			return "OK";
		});
	}

	public CacheStatistics getCacheStatistics(String cacheName) {
		return this.statistics.getCacheStatistics(cacheName);
	}

	public void clearStatistics(String name) {
		this.statistics.reset(name);
	}

	public RedisCacheWriter withStatisticsCollector(CacheStatisticsCollector cacheStatisticsCollector) {
		return new CustomRedisCacheWriter(this.connectionFactory, this.sleepTime, cacheStatisticsCollector);
	}

	/**
	 * Explicitly set a write lock on a cache.
	 *
	 * @param name the name of the cache to lock.
	 */
	void lock(String name) {
		execute(name, connection -> doLock(name, connection));
	}

	/**
	 * Explicitly remove a write lock from a cache.
	 *
	 * @param name the name of the cache to unlock.
	 */
	void unlock(String name) {
		executeLockFree(connection -> doUnlock(name, connection));
	}

	private Boolean doLock(String name, RedisConnection connection) {
		return connection.setNX(createCacheLockKey(name), new byte[0]);
	}

	private Long doUnlock(String name, RedisConnection connection) {
		return connection.del(createCacheLockKey(name));
	}

	boolean doCheckLock(String name, RedisConnection connection) {
		return connection.exists(createCacheLockKey(name));
	}

	/**
	 * @return {@literal true} if {@link RedisCacheWriter} uses locks.
	 */
	private boolean isLockingCacheWriter() {
		return !sleepTime.isZero() && !sleepTime.isNegative();
	}

	private <T> T execute(String name, Function<RedisConnection, T> callback) {
		RedisConnection connection = connectionFactory.getConnection();
		try {
			checkAndPotentiallyWaitUntilUnlocked(name, connection);
			return callback.apply(connection);
		} finally {
			connection.close();
		}
	}

	private void executeLockFree(Consumer<RedisConnection> callback) {
		RedisConnection connection = connectionFactory.getConnection();

		try {
			callback.accept(connection);
		} finally {
			connection.close();
		}
	}

	private void checkAndPotentiallyWaitUntilUnlocked(String name, RedisConnection connection) {
		if (!isLockingCacheWriter()) {
			return;
		}

		long lockWaitTimeNs = System.nanoTime();
		try {

			while (doCheckLock(name, connection)) {
				Thread.sleep(sleepTime.toMillis());
			}
		} catch (InterruptedException ex) {
			// Re-interrupt current thread, to allow other participants to react.
			Thread.currentThread().interrupt();

			throw new PessimisticLockingFailureException(String.format("Interrupted while waiting to unlock cache %s", name),
				ex);
		} finally {
			statistics.incLockTime(name, System.nanoTime() - lockWaitTimeNs);
		}
	}

}
