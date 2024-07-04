package com.chensoul.sharedlib;

import com.chensoul.sharedlib.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.time.Clock;
import java.util.TimeZone;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class CommonLibraryBeanFactory {
	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}

	@Bean
	@ConditionalOnMissingBean
	public ObjectMapper objectMapper() {
		return JsonUtils.getObjectMapperWithJavaTimeModule();
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
	}
}
