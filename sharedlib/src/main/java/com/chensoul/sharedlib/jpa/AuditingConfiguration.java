package com.chensoul.sharedlib.jpa;

import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Configuration(proxyBeanMethods = false)
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class AuditingConfiguration {

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new AuditorAwareImpl();
	}

	@Bean // Makes ZonedDateTime compatible with auditing fields
	public DateTimeProvider auditingDateTimeProvider() {
		return () -> Optional.of(ZonedDateTime.now());
	}

	@Component
	public class AuditorAwareImpl implements AuditorAware<String> {
		private static final String ANONYMOUS_USER = "anonymousUser";

		@Override
		public Optional<String> getCurrentAuditor() {
			return Optional.ofNullable(SecurityContextHolder.getContext())
				.map(SecurityContext::getAuthentication)
				.filter(Authentication::isAuthenticated)
				.map(Authentication::getPrincipal)
				.filter(principal -> !principal.equals(ANONYMOUS_USER))
				.map(it -> {
					if (it instanceof Jwt jwtToken) {
						return jwtToken.getSubject();
					}
					return null;
				});
		}
	}
}
