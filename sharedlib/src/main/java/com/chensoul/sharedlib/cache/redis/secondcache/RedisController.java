package com.chensoul.sharedlib.cache.redis.secondcache;

import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 1.0.0
 */
@RestController
@RequestMapping
@AllArgsConstructor
public class RedisController {
	private LocalCacheService localCacheService;
	private StringRedisTemplate template;

	@GetMapping("/get")
	public String get(String key) {
		return localCacheService.get(key).toString();
	}

	@GetMapping("/set")
	public String set(String key) {
		String value = UUID.randomUUID().toString();
		template.opsForValue().set(key, value);

		return value;
	}

}
