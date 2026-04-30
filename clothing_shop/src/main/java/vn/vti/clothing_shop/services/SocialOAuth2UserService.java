package vn.vti.clothing_shop.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.constants.SocialAuthProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class SocialOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = delegate.loadUser(userRequest);
        if (!SocialAuthProvider.TWITTER.registrationId().equals(userRequest.getClientRegistration().getRegistrationId())) {
            return user;
        }

        Map<String, Object> attributes = new HashMap<>(user.getAttributes());
        Map<String, Object> data = nestedData(attributes);
        if (data != null) {
            attributes.putAll(data);
        }

        Set<GrantedAuthority> authorities = Set.copyOf(user.getAuthorities());
        return new DefaultOAuth2User(authorities, attributes, "id");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> nestedData(Map<String, Object> attributes) {
        Object value = attributes.get("data");
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }
}
