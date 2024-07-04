/**
 * Copyright © 2016-2024 The Thingsboard Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chensoul.sharedlib.cache;

import com.chensoul.sharedlib.util.concurrent.GroupedThreadFactory;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Ticker;
import com.github.benmanes.caffeine.cache.Weigher;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheManagerProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Data
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "caffeine")
@EnableConfigurationProperties(CacheSpecProperties.class)
public class CaffeineConfiguration {

	private final CacheSpecProperties cacheSpecProperties;
	List<CaffeineCache> caches = Collections.emptyList();
	@Value("${spring.cache.caffeine.stats.enabled:true}")
	private boolean cacheStatsEnabled;
	@Value("${spring.cache.caffeine.stats.interval:60}")
	private Duration cacheStatsInterval;
	private ScheduledExecutorService scheduler = null;

	@PostConstruct
	public void init() {
		if (cacheStatsEnabled) {
			log.info("Initializing caffeine cache stats scheduled job");
			scheduler = Executors.newSingleThreadScheduledExecutor(GroupedThreadFactory.forName("caffeine-stats"));
			scheduler.scheduleAtFixedRate(this::printCacheStats, cacheStatsInterval.getSeconds(), cacheStatsInterval.getSeconds(), TimeUnit.SECONDS);
		}
	}

	@PreDestroy
	public void destroy() {
		if (scheduler != null) {
			scheduler.shutdown();
		}
	}

	@Bean
	public CacheManager cacheManager() {
		log.info("Initializing caffeine cache: {}, specs {}", Arrays.toString(RemovalCause.values()), cacheSpecProperties.getSpecs());

		SimpleCacheManager manager = new SimpleCacheManager();
		if (cacheSpecProperties.getSpecs() != null) {
			caches =
				cacheSpecProperties.getSpecs().entrySet().stream()
					.map(entry -> buildCache(entry.getKey(),
						entry.getValue()))
					.collect(Collectors.toList());
			manager.setCaches(caches);
		}

		//SimpleCacheManager is not a bean (will be wrapped), so call initializeCaches manually
		manager.initializeCaches();

		return new TransactionAwareCacheManagerProxy(manager);
	}

	void printCacheStats() {
		caches.forEach((cache) -> {
			CacheStats stats = cache.getNativeCache().stats();
			if (stats.hitCount() != 0 && stats.missCount() != 0) {
				log.info("Caffeine [{}]: hit rate [{}], hits [{}], misses [{}], puts [{}], deletes [{}]",
					cache.getName(), BigDecimal.valueOf(stats.hitRate()).setScale(2), stats.hitCount(), stats.missCount(), stats.requestCount(), stats.evictionCount());
			}
		});
	}

	private CaffeineCache buildCache(String name, CacheSpec cacheSpec) {
		final Caffeine<Object, Object> caffeineBuilder
			= Caffeine.newBuilder()
			.weigher(collectionSafeWeigher())
			.maximumWeight(cacheSpec.getMaxSize())
			.expireAfterWrite(cacheSpec.getTimeToLive().toMinutes(), TimeUnit.MINUTES)
			.ticker(ticker());
		if (cacheStatsEnabled) {
			caffeineBuilder.recordStats();
		}
		return new CaffeineCache(name, caffeineBuilder.build());
	}

	@Bean
	public Ticker ticker() {
		return Ticker.systemTicker();
	}

	private Weigher<? super Object, ? super Object> collectionSafeWeigher() {
		return (Weigher<Object, Object>) (key, value) -> {
			if (value instanceof Collection) {
				return ((Collection) value).size();
			}
			return 1;
		};
	}
}
