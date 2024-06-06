package com.chensoul.monolith.infrastructure.cache.secondcache;

import com.github.benmanes.caffeine.cache.Cache;
import io.lettuce.core.RedisClient;
import io.lettuce.core.TrackingArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.support.caching.CacheFrontend;
import io.lettuce.core.support.caching.ClientSideCaching;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.scheduling.TaskScheduler;

/**
 * Local cache Service
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class LocalCacheService implements CacheFrontend {
	private final LettuceConnectionFactory connectionFactory;
	private final Cache cache;
	private final TaskScheduler taskScheduler;

	private CacheFrontend cacheFrontend;
	private StatefulRedisConnection<String, String> connection;

	@PostConstruct
	public void init() {
		//junit 测试不支持异步线程池
		taskScheduler.scheduleAtFixedRate(() -> {
			check();
		}, 1000); // 1 second
	}

	public void check() {
		if (connection != null && connection.isOpen()) {
			return;
		}

		try {
			connection = ((RedisClient) connectionFactory.getNativeClient()).connect();
			this.cacheFrontend = ClientSideCaching.enable(new CaffeineCacheAccessor(cache), connection, TrackingArgs.Builder.enabled());

			connection.addListener(message -> {
					log.info("push message: {}", message);

					List<Object> content = message.getContent(StringCodec.UTF8::decodeKey);
					if (message.getType().equals("invalidate")) {
						List<String> keys = (List<String>) content.get(1);
						log.info("invalidate keys: {}", keys);
						keys.forEach(key -> cache.invalidate(key));
					}
				}
			);
			log.warn("The redis client connection has been reconnected to {}:{}", connectionFactory.getHostName(), connectionFactory.getPort());
		} catch (Exception e) {
			log.error("The redis client connection to {}:{} has been closed", connectionFactory.getHostName(), connectionFactory.getPort());
		}
	}

	@Override
	public Object get(Object o) {
		return cacheFrontend.get(o);
	}

	@Override
	public Object get(Object o, Callable callable) {
		return cacheFrontend.get(o, callable);
	}

	@Override
	public void close() {
		cacheFrontend.close();
	}
}
