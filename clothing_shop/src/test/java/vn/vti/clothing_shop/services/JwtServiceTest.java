package vn.vti.clothing_shop.services;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import vn.vti.clothing_shop.entities.User;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {
	private static final String SECRET = Base64.getEncoder()
	                                           .encodeToString(
			                                           "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));

	@Test
	void generatesAndValidatesTokenClaims() {
		JwtService service = serviceWithExpiration(60_000);
		User user = user(42L, "pepper");

		String salt = service.generateSalt(String.valueOf(user.getId()));
		String token = service.generateToken(user);
		String extractedSalt = service.extractClaim(token, claims -> claims.get("salt", String.class));

		assertThat(service.extractId(salt)).isEqualTo("42");
		assertThat(service.extractId(token)).isEqualTo("42");
		assertThat(extractedSalt).isEqualTo("pepper");
		assertThat(service.getExpirationTime()).isEqualTo(60_000);
		assertThat(service.isTokenExpired(token)).isFalse();
		assertThat(service.isTokenValid(token, user)).isTrue();
		assertThat(service.isTokenValid(token, user(7L, "pepper"))).isFalse();
	}

	@Test
	void detectsExpiredToken() {
		JwtService service = serviceWithExpiration(-1_000);
		String token = service.generateToken(user(5L, "old"));

		assertThatThrownBy(() -> service.isTokenExpired(token)).hasMessageContaining("JWT expired");
		assertThatThrownBy(() -> service.isTokenValid(token, user(5L, "old"))).hasMessageContaining("JWT expired");
	}

	private static JwtService serviceWithExpiration(long expirationMillis) {
		JwtService service = new JwtService();
		ReflectionTestUtils.setField(service, "secretKey", SECRET);
		ReflectionTestUtils.setField(service, "jwtExpiration", expirationMillis);
		return service;
	}

	private static User user(Long id, String salt) {
		User user = new User();
		user.setId(id);
		user.setSalt(salt);
		return user;
	}
}
