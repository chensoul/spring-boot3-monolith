package com.chensoul.sharedlib.errorhandler;

import static com.chensoul.sharedlib.Constants.LOCAL_DATETIME_SERIALIZER;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.client.HttpClientErrorException;

@Configuration
public class ErrorConfiguration {
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder().serializers(LOCAL_DATETIME_SERIALIZER)
			.serializationInclusion(JsonInclude.Include.NON_NULL);
		return new MappingJackson2HttpMessageConverter(builder.build());
	}

	@Bean
	@Order(-2)
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
	public GlobalExceptionHandler reactiveExceptionHandler() {
		return new GlobalExceptionHandler(exceptionToStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Bean("exceptionToStatusCode")
	public Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode() {
		Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode = new HashMap<>();
		exceptionToStatusCode.put(IllegalArgumentException.class, HttpStatus.BAD_REQUEST);
		exceptionToStatusCode.put(BadJwtException.class, HttpStatus.BAD_REQUEST);
		exceptionToStatusCode.put(AuthenticationCredentialsNotFoundException.class, HttpStatus.UNAUTHORIZED);
		exceptionToStatusCode.put(HttpClientErrorException.Unauthorized.class, HttpStatus.UNAUTHORIZED);
		exceptionToStatusCode.put(HttpClientErrorException.Forbidden.class, HttpStatus.FORBIDDEN);
		exceptionToStatusCode.put(HttpClientErrorException.BadRequest.class, HttpStatus.BAD_REQUEST);
		exceptionToStatusCode.put(HttpClientErrorException.Conflict.class, HttpStatus.CONFLICT);
		exceptionToStatusCode.put(JwtValidationException.class, HttpStatus.UNAUTHORIZED);
		exceptionToStatusCode.put(BadCredentialsException.class, HttpStatus.UNAUTHORIZED);
		return exceptionToStatusCode;
	}

	@Bean
	public HttpStatus defaultStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
