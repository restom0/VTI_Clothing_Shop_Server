package vn.vti.clothing_shop.constants;

import java.util.Locale;

public enum SocialAuthProvider {
	GOOGLE,
	FACEBOOK,
	TWITTER;

	public static SocialAuthProvider fromRegistrationId(String registrationId) {
		return SocialAuthProvider.valueOf(registrationId.toUpperCase(Locale.ROOT));
	}

	public String registrationId() {
		return name().toLowerCase(Locale.ROOT);
	}
}
