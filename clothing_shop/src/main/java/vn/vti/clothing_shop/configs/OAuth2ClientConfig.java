package vn.vti.clothing_shop.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ClientSettings;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import vn.vti.clothing_shop.constants.SocialAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OAuth2ClientConfig {
    private static final String REDIRECT_URI = "{baseUrl}/login/oauth2/code/{registrationId}";

    private final OAuth2LoginProperties properties;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();
        addIfConfigured(registrations, googleRegistration());
        addIfConfigured(registrations, facebookRegistration());
        addIfConfigured(registrations, twitterRegistration());
        return new OptionalClientRegistrationRepository(registrations);
    }

    private ClientRegistration googleRegistration() {
        OAuth2LoginProperties.Provider google = properties.getClient().getGoogle();
        if (!isConfigured(google)) {
            return null;
        }
        return CommonOAuth2Provider.GOOGLE.getBuilder(SocialAuthProvider.GOOGLE.registrationId())
                .clientId(google.getClientId())
                .clientSecret(google.getClientSecret())
                .redirectUri(REDIRECT_URI)
                .scope(scopesOrDefault(google.getScopes(), "openid", "profile", "email"))
                .build();
    }

    private ClientRegistration facebookRegistration() {
        OAuth2LoginProperties.Provider facebook = properties.getClient().getFacebook();
        if (!isConfigured(facebook)) {
            return null;
        }
        return CommonOAuth2Provider.FACEBOOK.getBuilder(SocialAuthProvider.FACEBOOK.registrationId())
                .clientId(facebook.getClientId())
                .clientSecret(facebook.getClientSecret())
                .redirectUri(REDIRECT_URI)
                .scope(scopesOrDefault(facebook.getScopes(), "public_profile", "email"))
                .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,picture")
                .build();
    }

    private ClientRegistration twitterRegistration() {
        OAuth2LoginProperties.TwitterProvider twitter = properties.getClient().getTwitter();
        if (!isConfigured(twitter)) {
            return null;
        }
        return ClientRegistration.withRegistrationId(SocialAuthProvider.TWITTER.registrationId())
                .clientName("X")
                .clientId(twitter.getClientId())
                .clientSecret(twitter.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(REDIRECT_URI)
                .scope(scopesOrDefault(twitter.getScopes(), "tweet.read", "users.read", "users.email"))
                .authorizationUri(twitter.getAuthorizationUri())
                .tokenUri(twitter.getTokenUri())
                .userInfoUri(twitter.getUserInfoUri())
                .userNameAttributeName("data")
                .clientSettings(ClientSettings.builder().requireProofKey(true).build())
                .build();
    }

    private List<String> scopesOrDefault(List<String> scopes, String... defaults) {
        if (scopes == null || scopes.isEmpty()) {
            return Arrays.asList(defaults);
        }
        return scopes;
    }

    private void addIfConfigured(List<ClientRegistration> registrations, ClientRegistration registration) {
        if (registration != null) {
            registrations.add(registration);
        }
    }

    private boolean isConfigured(OAuth2LoginProperties.Provider provider) {
        return hasText(provider.getClientId()) && hasText(provider.getClientSecret());
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static final class OptionalClientRegistrationRepository implements ClientRegistrationRepository, Iterable<ClientRegistration> {
        private final Map<String, ClientRegistration> registrations;

        private OptionalClientRegistrationRepository(List<ClientRegistration> registrations) {
            this.registrations = new LinkedHashMap<>();
            for (ClientRegistration registration : registrations) {
                this.registrations.put(registration.getRegistrationId(), registration);
            }
        }

        @Override
        public ClientRegistration findByRegistrationId(String registrationId) {
            return registrations.get(registrationId);
        }

        @Override
        public Iterator<ClientRegistration> iterator() {
            return registrations.values().iterator();
        }
    }
}
