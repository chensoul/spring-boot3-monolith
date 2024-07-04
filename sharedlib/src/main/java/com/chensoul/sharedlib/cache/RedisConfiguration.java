package com.chensoul.sharedlib.cache;

import com.chensoul.sharedlib.cache.redis.CustomRedisCacheManager;
import com.chensoul.sharedlib.cache.redis.CustomRedisCacheWriter;
import com.chensoul.sharedlib.cache.redis.RedisObjectFactory;
import com.chensoul.sharedlib.cache.redis.RedisOperationMetricsInterceptor;
import com.chensoul.sharedlib.cache.redis.RedisTemplateBeanPostProcessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.CollectionUtils;

@Slf4j
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(RedisConnectionFactory.class)
@ConditionalOnMissingBean(CacheManager.class)
@EnableConfigurationProperties({CacheProperties.class, CacheSpecProperties.class})
public class RedisConfiguration {

	@Bean
	public CacheManagerCustomizers cacheManagerCustomizers(
		ObjectProvider<List<CacheManagerCustomizer<?>>> customizers) {
		return new CacheManagerCustomizers(customizers.getIfAvailable());
	}

	@Bean
	RedisCacheManager cacheManager(CacheProperties cacheProperties,
								   CacheSpecProperties cacheSpecProperties,
								   CacheManagerCustomizers cacheManagerCustomizers,
								   ObjectProvider<RedisCacheManagerBuilderCustomizer> redisCacheManagerBuilderCustomizers,
								   RedisConnectionFactory redisConnectionFactory) {
		DefaultFormattingConversionService redisConversionService = new DefaultFormattingConversionService();
		org.springframework.data.redis.cache.RedisCacheConfiguration.registerDefaultConverters(redisConversionService);
		org.springframework.data.redis.cache.RedisCacheConfiguration configuration = createConfiguration(cacheProperties, redisConversionService);

		Map<String, org.springframework.data.redis.cache.RedisCacheConfiguration> initialCaches = new HashMap<>();

		//支持通过配置文件来自定义缓存过期时间
		if (cacheSpecProperties != null && !CollectionUtils.isEmpty(cacheSpecProperties.getSpecs())) {
			log.info("Initialized redis cache specs {}", cacheSpecProperties.getSpecs());

			cacheSpecProperties.getSpecs().forEach((cacheName, cacheSpecs) -> {
				initialCaches.put(cacheName, configuration.entryTtl(cacheSpecs.getTimeToLive()));
			});
		}

		RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(configuration).withInitialCacheConfigurations(initialCaches).transactionAware();
		if (cacheProperties.getRedis().isEnableStatistics()) {
			builder.enableStatistics();
		}

		//支持通过缓存名称来自定义缓存过期时间
		final CustomRedisCacheWriter redisCacheWriter = new CustomRedisCacheWriter(redisConnectionFactory);
		final CustomRedisCacheManager cacheManager = new CustomRedisCacheManager(redisCacheWriter, configuration,
			initialCaches, true);

		redisCacheManagerBuilderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
		return cacheManagerCustomizers.customize(builder.build());
	}

	private RedisCacheConfiguration createConfiguration(
		CacheProperties cacheProperties, DefaultFormattingConversionService redisConversionService) {
		CacheProperties.Redis redisProperties = cacheProperties.getRedis();

		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
		config.withConversionService(redisConversionService);

		config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()));
		config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisObjectFactory.jackson2JsonRedisSerializer()));

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

	@Bean
	@ConditionalOnClass(RedisOperations.class)
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
													   Jackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);

		// 使用StringRedisSerializer来序列化和反序列化redis的key值
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

		// Hash的key也采用StringRedisSerializer的序列化方式
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.afterPropertiesSet();

		return redisTemplate;
	}

	@Bean
	public Jackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
			mapper, Object.class);
		return jackson2JsonRedisSerializer;
	}

	@Bean
	public DefaultRedisScript<Long> redisScript() {
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
		redisScript.setLocation(new ClassPathResource("limit.lua"));
		redisScript.setResultType(Long.class);
		return redisScript;
	}

	@Bean
	public RedisTemplateBeanPostProcessor redisTemplateBeanPostProcessor() {
		return new RedisTemplateBeanPostProcessor();
	}

	@Bean
	public RedisOperationMetricsInterceptor redisOperationMetricsInterceptor() {
		return new RedisOperationMetricsInterceptor();
	}
}
