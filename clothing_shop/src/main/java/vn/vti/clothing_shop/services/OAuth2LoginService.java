package vn.vti.clothing_shop.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.constants.SocialAuthProvider;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.entities.UserSocialAccount;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.repositories.UserSocialAccountRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2LoginService {
	private final UserRepository userRepository;
	private final UserSocialAccountRepository socialAccountRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Transactional
	public UserLoginDTO login(String registrationId, Map<String, Object> attributes) {
		SocialAuthProvider provider = SocialAuthProvider.fromRegistrationId(registrationId);
		SocialProfile profile = SocialProfile.from(provider, attributes);

		UserSocialAccount socialAccount = socialAccountRepository
				.findByDeletedAtIsNullAndProviderAndProviderUserId(provider, profile.providerUserId())
				.orElseGet(() -> createSocialAccount(provider, profile));

		User user = syncUserProfile(socialAccount.getUser(), profile);
		syncSocialAccount(socialAccount, profile);

		return new UserLoginDTO(user.getAvatarUrl(), user.getName(), jwtService.generateToken(user), null);
	}

	private UserSocialAccount createSocialAccount(SocialAuthProvider provider, SocialProfile profile) {
		User user = findExistingUserByEmail(profile.email());
		if (user == null) {
			user = createUser(provider, profile);
		}

		UserSocialAccount socialAccount = new UserSocialAccount();
		socialAccount.setUser(user);
		socialAccount.setProvider(provider);
		socialAccount.setProviderUserId(profile.providerUserId());
		return socialAccount;
	}

	private User findExistingUserByEmail(String email) {
		if (email == null || email.isBlank()) {
			return null;
		}
		return userRepository.findByDeletedAtIsNullAndEmail(email).orElse(null);
	}

	private User createUser(SocialAuthProvider provider, SocialProfile profile) {
		User user = new User();
		user.setName(firstNonBlank(profile.name(), provider.registrationId() + " user"));
		user.setUsername(uniqueUsername(provider, profile));
		user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
		user.setAvatarUrl(profile.avatarUrl());
		user.setEmail(blankToNull(profile.email()));
		user.setPhoneNumber(uniquePlaceholderPhone(provider, profile.providerUserId()));
		user.setRole(UserRole.USER);
		user.setSalt(UUID.randomUUID().toString());
		user.setGender(UserGender.MALE);
		return userRepository.save(user);
	}

	private User syncUserProfile(User user, SocialProfile profile) {
		boolean changed = false;
		if ((user.getName() == null || user.getName().isBlank()) && hasText(profile.name())) {
			user.setName(profile.name());
			changed = true;
		}
		if ((user.getAvatarUrl() == null || user.getAvatarUrl().isBlank()) && hasText(profile.avatarUrl())) {
			user.setAvatarUrl(profile.avatarUrl());
			changed = true;
		}
		if ((user.getEmail() == null || user.getEmail().isBlank()) && hasText(profile.email())) {
			user.setEmail(profile.email());
			changed = true;
		}
		return changed ? userRepository.save(user) : user;
	}

	private void syncSocialAccount(UserSocialAccount socialAccount, SocialProfile profile) {
		socialAccount.setEmail(blankToNull(profile.email()));
		socialAccount.setName(firstNonBlank(profile.name(), socialAccount.getName()));
		socialAccount.setAvatarUrl(firstNonBlank(profile.avatarUrl(), socialAccount.getAvatarUrl()));
		socialAccountRepository.save(socialAccount);
	}

	private String uniqueUsername(SocialAuthProvider provider, SocialProfile profile) {
		String seed = firstNonBlank(profile.username(), profile.email(), profile.providerUserId());
		String base = sanitizeUsername(provider.registrationId() + "_" + seed);
		String username = base;
		int suffix = 1;
		while (userRepository.existsByDeletedAtIsNullAndUsername(username)) {
			username = base + "_" + suffix;
			suffix++;
		}
		return username;
	}

	private String uniquePlaceholderPhone(SocialAuthProvider provider, String providerUserId) {
		String base = "oauth-" + provider.registrationId() + "-" + shortHash(provider.registrationId() + ":" + providerUserId);
		String phoneNumber = base;
		int suffix = 1;
		while (userRepository.existsByDeletedAtIsNullAndPhoneNumber(phoneNumber)) {
			phoneNumber = base + "-" + suffix;
			suffix++;
		}
		return phoneNumber;
	}

	private String sanitizeUsername(String value) {
		String sanitized = value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "_");
		sanitized = sanitized.replaceAll("_+", "_").replaceAll("^_|_$", "");
		if (sanitized.isBlank()) {
			sanitized = "oauth_user";
		}
		return sanitized.length() <= 80 ? sanitized : sanitized.substring(0, 80);
	}

	private String shortHash(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
			return HexFormat.of().formatHex(hashed, 0, 8);
		} catch (NoSuchAlgorithmException exception) {
			return Integer.toHexString(value.hashCode());
		}
	}

	private boolean hasText(String value) {
		return value != null && !value.isBlank();
	}

	private String firstNonBlank(String... values) {
		for (String value : values) {
			if (hasText(value)) {
				return value;
			}
		}
		return null;
	}

	private String blankToNull(String value) {
		return hasText(value) ? value : null;
	}

	private record SocialProfile(
			String providerUserId,
			String name,
			String username,
			String email,
			String avatarUrl
	) {
		private static SocialProfile from(SocialAuthProvider provider, Map<String, Object> attributes) {
			return switch (provider) {
				case GOOGLE -> google(attributes);
				case FACEBOOK -> facebook(attributes);
				case TWITTER -> twitter(attributes);
			};
		}

		private static SocialProfile google(Map<String, Object> attributes) {
			return new SocialProfile(
					requiredString(attributes, "sub"),
					string(attributes, "name"),
					string(attributes, "email"),
					string(attributes, "email"),
					string(attributes, "picture")
			);
		}

		private static SocialProfile facebook(Map<String, Object> attributes) {
			return new SocialProfile(
					requiredString(attributes, "id"),
					string(attributes, "name"),
					string(attributes, "email"),
					string(attributes, "email"),
					facebookPicture(attributes)
			);
		}

		private static SocialProfile twitter(Map<String, Object> attributes) {
			Map<String, Object> data = nestedMap(attributes, "data");
			Map<String, Object> source = data == null ? attributes : data;
			return new SocialProfile(
					requiredString(source, "id"),
					string(source, "name"),
					string(source, "username"),
					string(source, "email"),
					string(source, "profile_image_url")
			);
		}

		private static String facebookPicture(Map<String, Object> attributes) {
			Map<String, Object> picture = nestedMap(attributes, "picture");
			Map<String, Object> data = picture == null ? null : nestedMap(picture, "data");
			return data == null ? null : string(data, "url");
		}

		@SuppressWarnings("unchecked")
		private static Map<String, Object> nestedMap(Map<String, Object> attributes, String key) {
			Object value = attributes.get(key);
			if (value instanceof Map<?, ?> map) {
				return (Map<String, Object>) map;
			}
			return null;
		}

		private static String requiredString(Map<String, Object> attributes, String key) {
			String value = string(attributes, key);
			if (value == null || value.isBlank()) {
				throw new IllegalArgumentException("Missing OAuth2 profile attribute: " + key);
			}
			return value;
		}

		private static String string(Map<String, Object> attributes, String key) {
			Object value = attributes.get(key);
			return value == null ? null : String.valueOf(value);
		}
	}
}
