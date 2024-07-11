package com.chensoul.sharedlib.security;

import static com.chensoul.sharedlib.security.util.SecurityUtils.PUBLIC_ENDPOINTS;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {
	private final AuthenticationErrorHandler authenticationErrorHandler = new AuthenticationErrorHandler(new ObjectMapper());

	@ConditionalOnProperty(prefix = "application.security", name = "enabled", havingValue = "true")
	@Bean
	public SecurityFilterChain securedFilterChain(final HttpSecurity http) throws Exception {
		log.warn("Secured FilterChain enabled");
		http.csrf(Customizer.withDefaults())
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
			.build();
	}

	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(new CustomAuthoritiesConverter());
		return converter;
	}

	public class CustomAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
		@Override
		public Collection<GrantedAuthority> convert(Jwt jwt) {
			return jwt.getClaimAsStringList("roles").stream()
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
		}
	}

}
