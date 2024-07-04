package com.chensoul.sharedlib.cache.redis;

import com.chensoul.sharedlib.cache.redis.bean.CustomRedisCacheManager;
import com.chensoul.sharedlib.cache.redis.bean.CustomRedisCacheWriter;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.lang.Nullable;

/**
 * 扩展redis-cache支持注解cacheName添加超时时间
 *
 * @author zhijun.chen
 * @since 0.0.1
 */
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
@AutoConfigureAfter(RedisCacheManagerAutoConfiguration.class)
@ConditionalOnMissingBean({CacheManager.class})
@EnableConfigurationProperties(CacheProperties.class)
public class RedisCacheAutoConfiguration {

	private final CacheProperties cacheProperties;

	private final CacheManagerCustomizers customizerInvoker;

	@Nullable
	private final RedisCacheConfiguration redisCacheConfiguration;

	RedisCacheAutoConfiguration(CacheProperties cacheProperties, CacheManagerCustomizers customizerInvoker,
								ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration) {
		this.cacheProperties = cacheProperties;
		this.customizerInvoker = customizerInvoker;
		this.redisCacheConfiguration = redisCacheConfiguration.getIfAvailable();
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, Jackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
		CustomRedisCacheWriter redisCacheWriter = new CustomRedisCacheWriter(connectionFactory, Duration.ZERO);
		RedisCacheConfiguration cacheConfiguration = this.determineConfiguration()
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));

		List<String> cacheNames = this.cacheProperties.getCacheNames();
		Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
		if (!cacheNames.isEmpty()) {
			Map<String, RedisCacheConfiguration> cacheConfigMap = new LinkedHashMap<>(cacheNames.size());
			cacheNames.forEach(it -> cacheConfigMap.put(it, cacheConfiguration));
			initialCaches.putAll(cacheConfigMap);
		}
		CustomRedisCacheManager cacheManager = new CustomRedisCacheManager(redisCacheWriter, cacheConfiguration,
			initialCaches, true);
		cacheManager.setTransactionAware(false);

		return this.customizerInvoker.customize(cacheManager);
	}

	private RedisCacheConfiguration determineConfiguration() {
		if (this.redisCacheConfiguration != null) {
			return this.redisCacheConfiguration;
		} else {
			CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
			RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

			if (redisProperties.getTimeToLive() != null) {
				config = config.entryTtl(redisProperties.getTimeToLive());
			}

			if (redisProperties.getKeyPrefix() != null) {
				config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
			}

			if (!redisProperties.isCacheNullValues()) {
				config = config.disableCachingNullValues();
			}

			if (!redisProperties.isUseKeyPrefix()) {
				config = config.disableKeyPrefix();
			}

			return config;
		}
	}

}
