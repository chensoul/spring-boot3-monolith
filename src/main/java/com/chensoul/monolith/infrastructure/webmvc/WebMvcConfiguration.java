package com.chensoul.monolith.infrastructure.webmvc;

import com.chensoul.monolith.infrastructure.webmvc.interceptors.LogSlowResponseTimeInterceptor;
import com.chensoul.monolith.infrastructure.webmvc.interceptors.TimeExecutionInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc Configurations
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since 0.0.1
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebMvcConfiguration implements WebMvcConfigurer {
	@Value("${miscellaneous.max-response-time-to-log-in-ms:3000}")
	private final int maxResponseTimeToLogInMs;

	@Override
	public void addInterceptors(final InterceptorRegistry registry) {
		registry.addInterceptor(new TimeExecutionInterceptor()).addPathPatterns("/**");
		registry
			.addInterceptor(new LogSlowResponseTimeInterceptor(this.maxResponseTimeToLogInMs))
			.addPathPatterns("/**");
	}

	@Bean
	public CommonsRequestLoggingFilter logFilter() {
		final CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter() {
			@Override
			protected void afterRequest(HttpServletRequest request, String message) {
			}
		};
		filter.setIncludeQueryString(true);
		filter.setIncludePayload(true);
		filter.setMaxPayloadLength(10000);
		filter.setIncludeHeaders(true);
		filter.setIncludeClientInfo(true);
		return filter;
	}
}


