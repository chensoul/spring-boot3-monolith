package com.chensoul.sharedlib.oss;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * aws 自动配置类
 */
@AllArgsConstructor
@ConditionalOnProperty(name = "oss.enabled", havingValue = "true")
@EnableConfigurationProperties({OssProperties.class})
public class OssAutoConfiguration {
	private final OssProperties properties;

	@Bean
	@ConditionalOnMissingBean(OssTemplate.class)
	public OssTemplate ossTemplate() {
		return new OssTemplate(properties);
	}

	@Bean
	public OssEndpoint ossEndpoint(OssTemplate template) {
		return new OssEndpoint(template);
	}

}
