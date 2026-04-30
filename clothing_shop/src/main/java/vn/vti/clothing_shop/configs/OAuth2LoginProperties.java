package vn.vti.clothing_shop.configs;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "application.oauth2")
public class OAuth2LoginProperties {
	private Login login = new Login();
	private Client client = new Client();

	@Data
	public static class Login {
		private String successRedirectUrl = "http://localhost:5173/oauth2/success";
		private String failureRedirectUrl = "http://localhost:5173/oauth2/failure";
	}

	@Data
	public static class Client {
		private Provider google = new Provider();
		private Provider facebook = new Provider();
		private TwitterProvider twitter = new TwitterProvider();
	}

	@Data
	public static class Provider {
		private String clientId = "";
		private String clientSecret = "";
		private List<String> scopes = new ArrayList<>();
	}

	@Data
	public static class TwitterProvider extends Provider {
		private String authorizationUri = "https://x.com/i/oauth2/authorize";
		private String tokenUri = "https://api.x.com/2/oauth2/token";
		private String userInfoUri = "https://api.x.com/2/users/me?user.fields=profile_image_url";
	}
}
