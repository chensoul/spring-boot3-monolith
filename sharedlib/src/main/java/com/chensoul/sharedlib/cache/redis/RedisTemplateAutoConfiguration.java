package com.chensoul.sharedlib.cache.redis;

import com.chensoul.sharedlib.cache.redis.bean.RedisOperationMetricsInterceptor;
import com.chensoul.sharedlib.cache.redis.bean.RedisTemplateBeanPostProcessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author zhijun.chen
 * @since 0.0.1
 */
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisTemplateAutoConfiguration {

	@Bean(name = "redisTemplate")
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
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
			Object.class);

		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		//将类型序列化到属性json字符串中
		mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
		jackson2JsonRedisSerializer.setObjectMapper(mapper);

		return jackson2JsonRedisSerializer;
	}

	@Bean
	@ConditionalOnBean(name = "redisTemplate")
	public RedisService redisService() {
		return new RedisService();
	}

	@Bean
	public RedisTemplateBeanPostProcessor redisTemplateBeanPostProcessor() {
		return new RedisTemplateBeanPostProcessor();
	}

	@Bean
	public RedisOperationMetricsInterceptor redisOperationMetricsInterceptor() {
		return new RedisOperationMetricsInterceptor();
	}

	@Bean
	public DefaultRedisScript<Long> limitScript() {
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptText(limitScriptText());
		redisScript.setResultType(Long.class);
		return redisScript;
	}

	/**
	 * 限流脚本
	 */
	private String limitScriptText() {
		return "local key = KEYS[1]\n" +
			   "local count = tonumber(ARGV[1])\n" +
			   "local time = tonumber(ARGV[2])\n" +
			   "local current = redis.call('get', key);\n" +
			   "if current and tonumber(current) > count then\n" +
			   "    return tonumber(current);\n" +
			   "end\n" +
			   "current = redis.call('incr', key)\n" +
			   "if tonumber(current) == 1 then\n" +
			   "    redis.call('expire', key, time)\n" +
			   "end\n" +
			   "return tonumber(current);";
	}

}
