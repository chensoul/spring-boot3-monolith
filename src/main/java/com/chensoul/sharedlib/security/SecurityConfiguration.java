package com.chensoul.sharedlib.security;

import static com.chensoul.sharedlib.security.util.SecurityUtils.PUBLIC_ENDPOINTS;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
@EnableWebSecurity
@Log4j2
public class SecurityConfiguration {
	private final AuthenticationErrorHandler authenticationErrorHandler = new AuthenticationErrorHandler(new ObjectMapper());

	@ConditionalOnProperty(prefix = "application.security", name = "enabled", havingValue = "true")
	@Bean
	public SecurityFilterChain securedFilterChain(final HttpSecurity http) throws Exception {
		log.warn("Secured FilterChain enabled");
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(authz -> authz
				.requestMatchers(PUBLIC_ENDPOINTS).permitAll()
				.requestMatchers("/api/**").authenticated())
			.cors(Customizer.withDefaults())
			.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(Customizer.withDefaults())
				.authenticationEntryPoint(authenticationErrorHandler));
		return http.build();
	}

	@ConditionalOnMissingBean(name = "securedFilterChain")
	@Bean
	public SecurityFilterChain defaultFilterChain(final HttpSecurity http) throws Exception {
		log.warn("Secured FilterChain disabled - exposing all endpoints");
		return http.authorizeRequests()
			.anyRequest().permitAll()
			.and()
			.csrf(csrf -> csrf.disable())
			.build();
	}
}
