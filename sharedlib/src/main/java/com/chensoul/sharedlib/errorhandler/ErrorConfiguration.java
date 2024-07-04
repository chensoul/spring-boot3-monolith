package com.chensoul.sharedlib.errorhandler;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.web.client.HttpClientErrorException;

@Configuration
public class ErrorConfiguration {

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
