package com.chensoul.monolith.infrastructure.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson Configurations
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Configuration
public class JacksonConfig {
	@Bean
	@ConditionalOnMissingBean
	public ObjectMapper objectMapper() {
		return JacksonUtil.getObjectMapperWithJavaTimeModule();
	}
}
