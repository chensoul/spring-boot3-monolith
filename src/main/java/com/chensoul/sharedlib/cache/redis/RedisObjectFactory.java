package com.chensoul.sharedlib.cache.redis;

import com.chensoul.sharedlib.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Slf4j
@UtilityClass
public class RedisObjectFactory {
	public static Jackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
		final ObjectMapper mapper = JsonUtils.getObjectMapperWithJavaTimeModule();
		return new Jackson2JsonRedisSerializer<>(mapper, Object.class);
	}
}
