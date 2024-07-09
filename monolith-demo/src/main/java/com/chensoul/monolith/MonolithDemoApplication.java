package com.chensoul.monolith;

import com.chensoul.sharedlib.EnableSharedLib;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
@EnableSharedLib
public class MonolithDemoApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(MonolithDemoApplication.class, args);

		String databaseUri = ctx.getEnvironment().getProperty("spring.datasource.url");
		log.info("Connected to database: " + databaseUri);
	}
}
