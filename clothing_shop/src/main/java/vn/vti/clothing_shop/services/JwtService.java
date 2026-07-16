package vn.vti.clothing_shop.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.entities.User;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date; // NOSONAR - JJWT 0.12.6 exposes java.util.Date in its public API.
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
	@Value("${security.jwt.secret-key}")
	private String secretKey;

	@Value("${security.jwt.expiration-time}")
	private long jwtExpiration;

	public String extractId(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String generateSalt(String id) {
		return buildSalt(id);
	}

	public String buildSalt(String id) {
		Instant now = Instant.now();
		return Jwts
				.builder()
				.subject(id)
				.issuedAt(toJwtDate(now))
				.expiration(toJwtDate(now.plusMillis(getExpirationTime())))
				.signWith(getSignInKey())
				.compact();
	}

	public long getExpirationTime() {
		return jwtExpiration;
	}

	private SecretKey getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(User user) {
		return buildToken(user);
	}

	public String buildToken(User user) {
		Instant now = Instant.now();
		Map<String, Object> claims = new HashMap<>();
		claims.put("salt", user.getSalt());
		return Jwts
				.builder()
				.claims(claims)
				.subject(String.valueOf(user.getId()))
				.issuedAt(toJwtDate(now))
				.expiration(toJwtDate(now.plusMillis(getExpirationTime())))
				.signWith(getSignInKey())
				.compact();
	}

	public boolean isTokenValid(String token, User user) {
		final String id = extractId(token);
		return (id.equals(String.valueOf(user.getId()))) && !isTokenExpired(token);
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).isBefore(Instant.now());
	}

	private Instant extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration).toInstant();
	}

	@SuppressWarnings({ "java:S2143", "squid:S2143" })
	private Date toJwtDate(Instant instant) {
		return Date.from(instant); // NOSONAR - JJWT builder requires java.util.Date.
	}

	private Claims extractAllClaims(String token) {
		return Jwts
				.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
}

