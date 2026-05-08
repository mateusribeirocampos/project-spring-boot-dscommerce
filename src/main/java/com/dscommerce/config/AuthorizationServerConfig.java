package com.dscommerce.config;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;

import com.dscommerce.config.customgrant.CustomPasswordAuthenticationConverter;
import com.dscommerce.config.customgrant.CustomPasswordAuthenticationProvider;
import com.dscommerce.config.customgrant.CustomUserAuthorities;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class AuthorizationServerConfig {

	@Value("${security.client-id}")
	private String clientId;

	@Value("${security.client-secret}")
	private String clientSecret;

	@Value("${security.jwt.duration-access}")
	private Integer jwtDurationMinutes;

	@Value("${security.jwt.duration-refresh}")
	private Integer jwtDurationDays;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	@Order(2)
	public SecurityFilterChain asSecurityFilterChain(HttpSecurity http, JWKSource<SecurityContext> jwkSource, OAuth2AuthorizationService authorizationService) throws Exception {

		http.securityMatcher("/oauth2/**", "/.well-known/**").with(OAuth2AuthorizationServerConfigurer.authorizationServer(), Customizer.withDefaults());

		// @formatter:off
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
			.tokenEndpoint(tokenEndpoint -> tokenEndpoint
				.accessTokenRequestConverter(new CustomPasswordAuthenticationConverter())
				.authenticationProvider(new CustomPasswordAuthenticationProvider(authorizationService, tokenGenerator(jwkSource), userDetailsService, passwordEncoder)));

		http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));
		// @formatter:on

		return http.build();
	}

	@Bean
	@Profile("!test")
	public OAuth2AuthorizationService authorizationService(
			JdbcTemplate jdbcTemplate,
			RegisteredClientRepository clientRepo
	) {
		return new JdbcOAuth2AuthorizationService(jdbcTemplate, clientRepo);
	}

	@Bean
	public OAuth2AuthorizationConsentService oauth2AuthorizationConsentService() {
		return new InMemoryOAuth2AuthorizationConsentService();
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		// @formatter:off
		RegisteredClient registeredClient = RegisteredClient
			.withId(UUID.randomUUID().toString())
			.clientId(clientId)
			.clientSecret(passwordEncoder.encode(clientSecret))
			.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
			.scope("read")
			.scope("write")
			// CLIENT_SECRET_BASIC: default OAuth2 client auth — sends credentials as HTTP Basic Auth header.
			// Required for Postman, curl, and any client that uses Authorization: Basic base64(clientId:secret).
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
			// CLIENT_SECRET_POST: allows client_id + client_secret as form params (needed for Swagger UI).
			.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
			// Custom "password" grant — NOT the deprecated OAuth2 Resource Owner Password Credentials (RFC 6749 §4.3).
			// Purpose-built grant for this SPA login flow, implemented in CustomPasswordAuthenticationProvider.
			.authorizationGrantType(new AuthorizationGrantType("password"))
			.tokenSettings(tokenSettings())
			.clientSettings(clientSettings())
			.build();
		// @formatter:on

		// Portfolio: single static client loaded from env vars — InMemoryRegisteredClientRepository is sufficient.
		// Production with multiple clients or dynamic registration: use JdbcRegisteredClientRepository.
		return new InMemoryRegisteredClientRepository(registeredClient);
	}

	@Bean
	public TokenSettings tokenSettings() {
		// @formatter:off
		return TokenSettings.builder()
			.accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
				.accessTokenTimeToLive(Duration.ofSeconds(jwtDurationMinutes))
				.refreshTokenTimeToLive(Duration.ofSeconds(jwtDurationDays))
				.reuseRefreshTokens(false)
			.build();
		// @formatter:on
	}

	@Bean
	public ClientSettings clientSettings() {
		return ClientSettings.builder().build();
	}

	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}

	@Bean
	public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator(JWKSource<SecurityContext> jwkSource) {
		NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);
		JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
		jwtGenerator.setJwtCustomizer(tokenCustomizer());
		OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
		OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
		return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
	}

	@Bean
	public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
		// @formatter:off
		// grant_type=password —> login
		return context -> {
			if (!context.getTokenType().getValue().equals("access_token")) return;

			OAuth2ClientAuthenticationToken principal = context.getPrincipal();
			Object details = principal.getDetails();

			if (details instanceof CustomUserAuthorities user) {
				List<String> authorities = user.getAuthorities().stream()
						.map(x -> x.getAuthority()).toList();
				context.getClaims()
						.claim("authorities", authorities)
						.claim("username", user.getUsername());
			} else {
				// grant_type=refresh_token — read claims of previous token
				OAuth2Authorization authorization = context.get(OAuth2Authorization.class);
				if (authorization != null) {
					OAuth2Authorization.Token<OAuth2AccessToken> existing =
							authorization.getAccessToken();
					if (existing != null && existing.getClaims() != null) {
						context.getClaims()
								.claim("authorities", existing.getClaims().get("authorities"))
								.claim("username", existing.getClaims().get("username"));
					}
				}
			}
		};
	}

	@Bean
	public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
		return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
	}

	// LEGACY: RSA generated in memory on startup.
	// Kept for reference — replaced by JwtKeyConfig + KeyStore (dscommerce-jwt.p12).
	// Tokens were invalidated on restart; multi-instance deployments broke (different keys per JVM).
	//
	// private static RSAKey generateRsa() { ... }
	// private static KeyPair generateRsaKey() { ... }
//	@Bean
//	public JWKSource<SecurityContext> jwkSource() {
//		RSAKey rsaKey = generateRsa();
//		JWKSet jwkSet = new JWKSet(rsaKey);
//		return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
//	}

	// RSA key pair generated on startup — all issued tokens are invalidated on restart.
	// Acceptable for portfolio (single instance). Production: load from KeyStore or secrets manager
	// so all instances share the same signing key.
//	private static RSAKey generateRsa() {
//		KeyPair keyPair = generateRsaKey();
//		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//		return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
//	}

//	private static KeyPair generateRsaKey() {
//		KeyPair keyPair;
//		try {
//			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//			keyPairGenerator.initialize(2048);
//			keyPair = keyPairGenerator.generateKeyPair();
//		} catch (Exception ex) {
//			throw new IllegalStateException(ex);
//		}
//		return keyPair;
//	}
}
