package vn.vti.clothing_shop.configs;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OAuth2ClientConfigTest {
	@Test
	void emptyProvidersProduceEmptyRepository() {
		ClientRegistrationRepository repository = new OAuth2ClientConfig(new OAuth2LoginProperties())
				.clientRegistrationRepository();

		assertThat(repository.findByRegistrationId("google")).isNull();
		assertThat(registrations(repository)).isEmpty();
	}

	@Test
	void configuredProvidersAreRegisteredWithDefaultAndCustomScopes() {
		OAuth2LoginProperties properties = new OAuth2LoginProperties();
		properties.getClient().getGoogle().setClientId("google-client");
		properties.getClient().getGoogle().setClientSecret("google-secret");
		properties.getClient().getGoogle().setScopes(List.of("email"));
		properties.getClient().getFacebook().setClientId("facebook-client");
		properties.getClient().getFacebook().setClientSecret("facebook-secret");
		properties.getClient().getTwitter().setClientId("twitter-client");
		properties.getClient().getTwitter().setClientSecret("twitter-secret");

		ClientRegistrationRepository repository = new OAuth2ClientConfig(properties).clientRegistrationRepository();

		ClientRegistration google = repository.findByRegistrationId("google");
		ClientRegistration facebook = repository.findByRegistrationId("facebook");
		ClientRegistration twitter = repository.findByRegistrationId("twitter");
		assertThat(registrations(repository)).extracting(ClientRegistration::getRegistrationId)
				.containsExactly("google", "facebook", "twitter");
		assertThat(google.getScopes()).containsExactly("email");
		assertThat(facebook.getScopes()).contains("public_profile", "email");
		assertThat(twitter.getScopes()).contains("tweet.read", "users.read", "users.email");
		assertThat(twitter.getClientSettings().isRequireProofKey()).isTrue();
	}

	private List<ClientRegistration> registrations(ClientRegistrationRepository repository) {
		List<ClientRegistration> registrations = new ArrayList<>();
		for (ClientRegistration registration : (Iterable<ClientRegistration>) repository) {
			registrations.add(registration);
		}
		return registrations;
	}
}
