package com.chensoul.sharedlib.springdoc;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import static org.springdoc.core.utils.Constants.SPRINGDOC_ENABLED;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = SPRINGDOC_ENABLED, matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SpringdocConfig {

	@Value("${server.port}")
	private String port;

	@Value("${openapi.prod-url:https://localhost}")
	private String prodUrl;

	@Bean
	public OpenAPI openAPI() {
		Server devServer = new Server();
		devServer.setUrl("http://localhost:" + port);
		devServer.setDescription("Server URL in Development environment");

		Server prodServer = new Server();
		prodServer.setUrl(prodUrl);
		prodServer.setDescription("Server URL in Production environment");

		Contact contact = new Contact();
		contact.setEmail("ichensoul@gmail.com");
		contact.setName("ChenSoul");
		contact.setUrl("https://blog.chensoul.cc");

		License mitLicense = new License().name("Apache License").url("https://www.apache.org/licenses/LICENSE-2.0.txt");

		Info info = new Info()
			.title("Spring Boot3 Monolith API")
			.version("1.0")
			.contact(contact)
			.description("This API exposes endpoints to manage charging sessions.").termsOfService("https://blog.chensoul.cc/terms")
			.license(mitLicense);

		return new OpenAPI().info(info).servers(List.of(devServer, prodServer));
	}
}
