package vn.vti.clothing_shop.middlewares;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import vn.vti.clothing_shop.configs.OAuth2LoginProperties;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.services.OAuth2LoginService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OAuth2LoginHandlersTest {
	@Test
	void failureHandlerRedirectsWithDefaultAndExplicitError() throws Exception {
		OAuth2LoginProperties properties = properties();
		OAuth2LoginFailureHandler handler = new OAuth2LoginFailureHandler(properties);

		MockHttpServletResponse blankResponse = new MockHttpServletResponse();
		handler.redirectFailure(blankResponse, " ");
		assertThat(blankResponse.getRedirectedUrl()).isEqualTo(
				"http://client.example/oauth2/failure?error=oauth2_login_failed");

		MockHttpServletResponse response = new MockHttpServletResponse();
		handler.onAuthenticationFailure(new MockHttpServletRequest(), response, new BadCredentialsException("Denied"));
		assertThat(response.getRedirectedUrl()).isEqualTo("http://client.example/oauth2/failure?error=Denied");
	}

	@Test
	void successHandlerRedirectsWithEncodedTokenFragment() throws Exception {
		OAuth2LoginProperties properties = properties();
		OAuth2LoginService oauth2LoginService = mock(OAuth2LoginService.class);
		OAuth2LoginFailureHandler failureHandler = new OAuth2LoginFailureHandler(properties);
		OAuth2LoginSuccessHandler handler = new OAuth2LoginSuccessHandler(oauth2LoginService, failureHandler,
		                                                                  properties);
		Map<String, Object> attributes = Map.of("sub", "google-123", "name", "Ada Lovelace");
		when(oauth2LoginService.login("google", attributes)).thenReturn(
				new UserLoginDTO("https://cdn.example/avatar 1.png", "Ada Lovelace", "jwt token", null));

		MockHttpServletResponse response = new MockHttpServletResponse();
		handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication(attributes));

		assertThat(response.getRedirectedUrl()).isEqualTo(
				"http://client.example/oauth2/success#token=jwt%20token&name=Ada%20Lovelace&avatarUrl=https%3A%2F%2Fcdn.example%2Favatar%201.png");
	}

	@Test
	void successHandlerDelegatesFailuresToFailureHandler() throws Exception {
		OAuth2LoginProperties properties = properties();
		OAuth2LoginService oauth2LoginService = mock(OAuth2LoginService.class);
		OAuth2LoginFailureHandler failureHandler = new OAuth2LoginFailureHandler(properties);
		OAuth2LoginSuccessHandler handler = new OAuth2LoginSuccessHandler(oauth2LoginService, failureHandler,
		                                                                  properties);
		Map<String, Object> attributes = Map.of("sub", "google-123", "name", "Ada Lovelace");
		when(oauth2LoginService.login("google", attributes)).thenThrow(new IllegalStateException("boom"));

		MockHttpServletResponse response = new MockHttpServletResponse();
		handler.onAuthenticationSuccess(new MockHttpServletRequest(), response, authentication(attributes));

		assertThat(response.getRedirectedUrl()).isEqualTo(
				"http://client.example/oauth2/failure?error=oauth2_login_failed");
	}

	private static OAuth2AuthenticationToken authentication(Map<String, Object> attributes) {
		OAuth2User user = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority("ROLE_USER")), attributes, "sub");
		return new OAuth2AuthenticationToken(user, user.getAuthorities(), "google");
	}

	private static OAuth2LoginProperties properties() {
		OAuth2LoginProperties properties = new OAuth2LoginProperties();
		properties.getLogin().setSuccessRedirectUrl("http://client.example/oauth2/success");
		properties.getLogin().setFailureRedirectUrl("http://client.example/oauth2/failure");
		return properties;
	}
}
