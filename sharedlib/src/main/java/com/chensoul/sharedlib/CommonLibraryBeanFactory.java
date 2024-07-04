package com.chensoul.sharedlib;

import com.chensoul.sharedlib.jackson.JacksonUtil;
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
		return JacksonUtil.getObjectMapperWithJavaTimeModule();
	}

	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
	}
}
