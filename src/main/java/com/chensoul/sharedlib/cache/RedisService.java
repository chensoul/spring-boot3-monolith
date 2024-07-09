package com.chensoul.sharedlib.cache;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author zhijun.chen
 * @since 0.0.1
 */
@SuppressWarnings("all")
public class RedisService {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	public Boolean set(String key, Object value) {
		try {
			this.redisTemplate.opsForValue().set(key, value);
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	public Boolean set(String key, Object value, Long time) {
		try {
			if (time > 0L) {
				this.redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
			} else {
				this.set(key, value);
			}
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	public Object get(String key) {
		ValueOperations valueOperations = redisTemplate.opsForValue();
		return valueOperations.get(key);
	}

	public Boolean expire(String key, Long time) {
		try {
			if (time > 0L) {
				this.redisTemplate.expire(key, time, TimeUnit.SECONDS);
			}
			return Boolean.TRUE;
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	public Long getExpire(String key) {
		return this.redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	public void del(String... key) {
		if (key != null && key.length > 0) {
			if (key.length == 1) {
				this.redisTemplate.delete(key[0]);
			} else {
				this.redisTemplate.delete(Arrays.asList(key));
			}
		}
	}
}
