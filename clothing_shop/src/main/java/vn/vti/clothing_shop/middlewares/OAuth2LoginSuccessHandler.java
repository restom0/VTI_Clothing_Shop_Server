package vn.vti.clothing_shop.middlewares;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

import vn.vti.clothing_shop.configs.OAuth2LoginProperties;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.services.OAuth2LoginService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2LoginSuccessHandler.class);

	private final OAuth2LoginService oauth2LoginService;
	private final OAuth2LoginFailureHandler failureHandler;
	private final OAuth2LoginProperties properties;

	@Override
	public void onAuthenticationSuccess(
			HttpServletRequest request,
			HttpServletResponse response,
			Authentication authentication
	) throws IOException, ServletException {
		try {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
			OAuth2User principal = oauth2Token.getPrincipal();
			UserLoginDTO login = oauth2LoginService.login(
					oauth2Token.getAuthorizedClientRegistrationId(),
					principal.getAttributes()
			);
			response.sendRedirect(successUrl(login));
		} catch (Exception exception) {
			LOGGER.error("OAuth2 login success handling failed", exception);
			failureHandler.redirectFailure(response, "oauth2_login_failed");
		}
	}

	private String successUrl(UserLoginDTO login) {
		String fragment = "token=" + encode(login.getToken())
				+ "&name=" + encode(login.getName())
				+ "&avatarUrl=" + encode(login.getAvatarUrl());
		return properties.getLogin().getSuccessRedirectUrl() + "#" + fragment;
	}

	private String encode(String value) {
		return UriUtils.encode(value == null ? "" : value, StandardCharsets.UTF_8);
	}
}
