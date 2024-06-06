package com.chensoul.monolith.infrastructure.springdoc;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {

	@Bean
	public OpenAPI springShopOpenAPI() {
		return new OpenAPI()
			.info(new Info().title("Spring Boot3 Monolith API")
				.description("Spring Boot3 Monolith Application")
				.version("0.0.1")
				.license(new License().name("MIT").url("https://blog.chensoul.cc")))
			.externalDocs(new ExternalDocumentation()
				.description("Spring Boot3 Monolith Documentation")
				.url("https://blog.chensoul.cc"));
	}

}
