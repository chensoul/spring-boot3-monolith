package com.chensoul.sharedlib.security.authentication;

import static com.chensoul.sharedlib.Constants.CONTEXT_ID;
import com.chensoul.sharedlib.security.user.UserContextPermissions;
import com.chensoul.sharedlib.security.user.UserContextPermissionsService;
import com.chensoul.sharedlib.security.user.UserContextRequest;
import com.chensoul.sharedlib.security.util.SecurityUtils;
import static com.chensoul.sharedlib.security.util.SecurityUtils.createTokenApiJwtDecoderFromContextId;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.Assert;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationServiceAuthenticationManager implements AuthenticationManager {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceAuthenticationManager.class);
	@Builder.Default
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
	String defaultContext;
	UserContextPermissionsService userContextPermissionsService;
	String jwkSetUri;
	@Builder.Default
	JwtDecoderFactory<ClientRegistration> jwtDecoderFactory = new AuthenticationJwtDecoderFactory();
	@Builder.Default
	ObjectMapper objectMapper = new ObjectMapper();

	@PostConstruct
	public void init() {
		Assert.notNull(userContextPermissionsService, "userContextPermissionsService must not be null");
		Assert.notNull(jwkSetUri, "jwkSetUri must not be null");
		Assert.notNull(jwtDecoderFactory, "jwtDecoderFactory must not be null");
		Assert.notNull(objectMapper, "objectMapper must not be null");
		Assert.notNull(messages, "messages must not be null");
		Assert.notNull(defaultContext, "defaultContext must not be null");
	}

	public Tuple2<UserDetails, Optional<Jwt>> retrieveUser(Authentication authentication) {
		String contextId = determineContext(authentication);
		logger.info("retrieveUser(Authentication authentication) authentication name => {} authentication.getName() => {}, context => {}", authentication.getClass().getName(),
			authentication.getName(), contextId);
		if (authentication instanceof BearerTokenAuthenticationToken) {
			BearerTokenAuthenticationToken bearerToken = (BearerTokenAuthenticationToken) authentication;
			Tuple2<BearerTokenAuthenticationToken, UserContextPermissions> tuple = getUserFromAutheticationService(bearerToken, contextId);
			return extractJwtFromToken(jwtDecoderFactory, jwkSetUri).apply(tuple);
		} else {
			throw new BadCredentialsException("Invalid Credentials");
		}
	}

	Function<Tuple2<BearerTokenAuthenticationToken, UserContextPermissions>, Tuple2<UserDetails, Optional<Jwt>>> extractJwtFromToken(JwtDecoderFactory jwtDecoderFactory, String jwkSetUri) {
		return tuple -> {
			JwtDecoder jwtDecoder = createTokenApiJwtDecoderFromContextId(jwtDecoderFactory, tuple.getT2().getContextId(), jwkSetUri);
			Optional<Jwt> jwt = Optional.empty();
			try {
				logger.info("################################################# tuple.getT1().getToken() => {}", tuple.getT1().getToken());
				jwt = Optional.of(jwtDecoder.decode(tuple.getT1().getToken()));
				logger.info("################################################# jwt => {}", jwt);
			} catch (Exception e) {
				logger.error("Error extracting jwt from token", e);
			}
			UserDetails userDetails = SecurityUtils.convertUserContextPermissionsToUserDetails(jwt).apply(tuple.getT2());
			return Tuples.of(userDetails, jwt);
		};
	}

	private Tuple2<BearerTokenAuthenticationToken, UserContextPermissions> getUserFromAutheticationService(BearerTokenAuthenticationToken token, String contextId) {
		UserContextPermissions userCtx = userContextPermissionsService.getUserContextByUserIdAndContextId(UserContextRequest.builder()
			.contextId(contextId)
			.token(token.getToken())
			.build());
		return Tuples.of(token, userCtx);
	}

	@Override
	public Authentication authenticate(Authentication authentication) {
		Tuple2<UserDetails, Optional<Jwt>> tuple = retrieveUser(authentication);
		defaultPreAuthenticationChecks(tuple.getT1());
		defaultPostAuthenticationChecks(tuple.getT1());
		return createJwtAuthenticationToken(tuple);
	}

	private JwtAuthenticationToken createJwtAuthenticationToken(Tuple2<UserDetails, Optional<Jwt>> tuple) {
         /*
            public User(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities)
			User newUser = new User(user.getUsername(), jwt.getTokenValue(), user.isEnabled(), user.isAccountNonExpired(),
                jwt.getExpiresAt().isBefore(Instant.now()),user.isAccountNonLocked(), user.getAuthorities());
             */
		UserDetails user = tuple.getT1();
		Jwt jwt = tuple.getT2().get();
		logger.info("################################################# user.getAuthorities() => {}", user.getAuthorities());
		JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, user.getAuthorities());
		return token;
	}

	private void defaultPreAuthenticationChecks(UserDetails user) {
		if (!user.isAccountNonLocked()) {
			this.logger.debug("User account is locked");
			throw new LockedException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked",
				"User account is locked"));
		}
		if (!user.isEnabled()) {
			this.logger.debug("User account is disabled");
			throw new DisabledException(
				this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
		}
		if (!user.isAccountNonExpired()) {
			this.logger.debug("User account is expired");
			throw new AccountExpiredException(this.messages
				.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
		}
	}

	private void defaultPostAuthenticationChecks(UserDetails user) {
		if (!user.isCredentialsNonExpired()) {
			this.logger.debug("User account credentials have expired");
			throw new CredentialsExpiredException(this.messages.getMessage(
				"AbstractUserDetailsAuthenticationProvider.credentialsExpired", "User credentials have expired"));
		}
	}

	String determineContext(Authentication authentication) {
		return authentication.getDetails() != null ? ((Map<String, String>) authentication.getDetails()).getOrDefault(CONTEXT_ID, defaultContext)
			: defaultContext;
	}
}
