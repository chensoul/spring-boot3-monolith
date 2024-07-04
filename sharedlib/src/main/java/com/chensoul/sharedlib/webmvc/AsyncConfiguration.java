package com.chensoul.sharedlib.webmvc;

import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "application.async", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableAsync
@Slf4j
public class AsyncConfiguration implements AsyncConfigurer {
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (throwable, method, obj) -> {
			log.error("Exception Caught in Thread - " + Thread.currentThread().getName());
			log.error("Exception message - " + throwable.getMessage());
			log.error("Method name - " + method.getName());
			for (Object param : obj) {
				log.error("Parameter value - " + param);
			}
			throwable.printStackTrace();
		};
	}

	@Primary
	@Bean
	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(1);
		threadPoolTaskExecutor.setMaxPoolSize(4);
		threadPoolTaskExecutor.setThreadNamePrefix("AsyncTask-");
		threadPoolTaskExecutor.initialize();
		return threadPoolTaskExecutor;
	}
}
