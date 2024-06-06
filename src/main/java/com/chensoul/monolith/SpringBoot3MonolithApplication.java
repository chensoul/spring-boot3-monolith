package com.chensoul.monolith;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@EnableCaching
@ServletComponentScan
@SpringBootApplication
public class SpringBoot3MonolithApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(SpringBoot3MonolithApplication.class, args);

		String mysqlUri = ctx.getEnvironment().getProperty("spring.datasource.url");
		log.info("Connected to MySQL: " + mysqlUri);
	}
}
