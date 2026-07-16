package vn.vti.clothing_shop.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SocialOAuth2UserServiceTest {
	private final SocialOAuth2UserService service = new SocialOAuth2UserService();
	private DefaultOAuth2UserService delegate;

	@BeforeEach
	void setUp() {
		delegate = mock(DefaultOAuth2UserService.class);
		ReflectionTestUtils.setField(service, "delegate", delegate);
	}

	@Test
	void nonTwitterUsersAreReturnedWithoutAttributeFlattening() {
		OAuth2User user = oauthUser(Map.of("id", "42", "data", Map.of("username", "nested")));
		OAuth2UserRequest request = request("google");
		when(delegate.loadUser(request)).thenReturn(user);

		assertThat(service.loadUser(request)).isSameAs(user);
	}

	@Test
	void twitterUsersFlattenNestedDataAttributes() {
		OAuth2User user = oauthUser(Map.of(
				"id",
				"outer",
				"data",
				Map.of("id", "42", "username", "vti_shop", "name", "VTI Shop")
		));
		OAuth2UserRequest request = request("twitter");
		when(delegate.loadUser(request)).thenReturn(user);

		OAuth2User flattened = service.loadUser(request);

		assertThat(flattened).isNotSameAs(user);
		assertThat(flattened.getName()).isEqualTo("42");
		assertThat(flattened.getAttributes()).containsEntry("username", "vti_shop");
		assertThat(flattened.getAuthorities()).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_USER");
	}

	private OAuth2User oauthUser(Map<String, Object> attributes) {
		return new DefaultOAuth2User(
				List.of(new SimpleGrantedAuthority("ROLE_USER")),
				attributes,
				"id"
		);
	}

	private OAuth2UserRequest request(String registrationId) {
		OAuth2AccessToken token = new OAuth2AccessToken(
				OAuth2AccessToken.TokenType.BEARER,
				"token",
				Instant.now(),
				Instant.now().plusSeconds(60)
		);
		return new OAuth2UserRequest(registration(registrationId), token);
	}

	private ClientRegistration registration(String registrationId) {
		return ClientRegistration.withRegistrationId(registrationId)
				.clientId(registrationId + "-client")
				.clientSecret(registrationId + "-secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
				.authorizationUri("https://example.test/oauth/authorize")
				.tokenUri("https://example.test/oauth/token")
				.userInfoUri("https://example.test/userinfo")
				.userNameAttributeName("id")
				.clientName(registrationId)
				.build();
	}
}
