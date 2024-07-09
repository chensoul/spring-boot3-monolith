package com.chensoul.sharedlib.security.authentication;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Builder
@Getter
@NoArgsConstructor
public class AuthenticationJwtDecoderFactory implements JwtDecoderFactory<ClientRegistration> {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationJwtDecoderFactory.class);

	private final Map<String, JwtDecoder> jwtDecoders = new ConcurrentHashMap<>();

	@Override
	public JwtDecoder createDecoder(ClientRegistration clientRegistration) {
		return this.jwtDecoders.computeIfAbsent(clientRegistration.getRegistrationId(), (key) -> {
			String jwkSetUri = clientRegistration.getProviderDetails().getJwkSetUri();
			NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
			nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefault());
			return nimbusJwtDecoder;
		});
	}

}
