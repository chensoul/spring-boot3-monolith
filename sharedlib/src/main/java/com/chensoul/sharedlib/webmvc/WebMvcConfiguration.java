package com.chensoul.sharedlib.webmvc;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
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

	@Slf4j
	public static class LogSlowResponseTimeInterceptor implements HandlerInterceptor {

		private static final String EXEC_TIME = "execTime";
		private final int maxResponseTimeToLogInMs;

		public LogSlowResponseTimeInterceptor(final int maxResponseTimeToLogInMs) {
			this.maxResponseTimeToLogInMs = maxResponseTimeToLogInMs;
		}

		@Override
		public boolean preHandle(
			final HttpServletRequest request,
			final @NonNull HttpServletResponse response,
			final @NonNull Object handler) {
			request.setAttribute(EXEC_TIME, System.nanoTime());
			return true;
		}

		@Override
		public void postHandle(
			final HttpServletRequest request,
			final @NonNull HttpServletResponse response,
			final @NonNull Object handler,
			final ModelAndView modelAndView) {
			final Long startTime = (Long) request.getAttribute(EXEC_TIME);
			if (startTime != null) {
				final long elapsedInNanoS = System.nanoTime() - startTime;
				final long responseTimeInMs = elapsedInNanoS / 1_000_000;
				if (responseTimeInMs > this.maxResponseTimeToLogInMs) {
					log.warn(
						"[SLOW_REQUEST] {}ms {} '{}'",
						responseTimeInMs,
						request.getMethod(),
						request.getRequestURI());
				}
			}
		}
	}

	public class TimeExecutionInterceptor implements HandlerInterceptor {
		private static final String TIME = "StopWatch";

		@Override
		public boolean preHandle(
			final HttpServletRequest request,
			@NonNull final HttpServletResponse response,
			@NonNull final Object handler) {
			final long nano = System.nanoTime();

			request.setAttribute(TIME, nano);
			return true;
		}
	}
}
