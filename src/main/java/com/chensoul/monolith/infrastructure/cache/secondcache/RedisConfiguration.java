package com.chensoul.monolith.infrastructure.cache.secondcache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.scheduling.TaskScheduler;

/**
 * Redis Configuration
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@Configuration
public class RedisConfiguration {
	@Bean
	public Cache<String, Object> localCahce() {
		return Caffeine.newBuilder()
			.initialCapacity(100)
			.maximumSize(1000)
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.build();
	}

	@Bean
	public LocalCacheService localCacheService(LettuceConnectionFactory connectionFactory, Cache cache, TaskScheduler taskScheduler) {
		return new LocalCacheService(connectionFactory, cache, taskScheduler);
	}
}
