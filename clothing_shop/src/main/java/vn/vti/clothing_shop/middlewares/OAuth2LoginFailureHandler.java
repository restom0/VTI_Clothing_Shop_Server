package vn.vti.clothing_shop.middlewares;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import vn.vti.clothing_shop.configs.OAuth2LoginProperties;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {
    private final OAuth2LoginProperties properties;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        redirectFailure(response, exception.getMessage());
    }

    public void redirectFailure(HttpServletResponse response, String error) throws IOException {
        String redirectUrl = UriComponentsBuilder
                .fromUriString(properties.getLogin().getFailureRedirectUrl())
                .queryParam("error", error == null || error.isBlank() ? "oauth2_login_failed" : error)
                .build()
                .encode()
                .toUriString();
        response.sendRedirect(redirectUrl);
    }
}
