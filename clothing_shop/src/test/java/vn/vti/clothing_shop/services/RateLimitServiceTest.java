package vn.vti.clothing_shop.services;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import vn.vti.clothing_shop.configs.RateLimitProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitServiceTest {

	@Test
	void appliesConfiguredRouteLimitByClientIp() {
		RateLimitProperties properties = propertiesWithDefaultCapacity(100);
		RateLimitProperties.Policy authPolicy = policy("auth", 1, "/user/login", "POST");
		properties.setRoutes(new ArrayList<>(List.of(authPolicy)));
		RateLimitService service = new RateLimitService(properties);

		MockHttpServletRequest request = request("POST", "/api/user/login", "10.0.0.10");

		assertThat(service.consume(request).allowed()).isTrue();
		RateLimitService.RateLimitDecision blocked = service.consume(request);

		assertThat(blocked.allowed()).isFalse();
		assertThat(blocked.policyName()).isEqualTo("auth");
		assertThat(blocked.retryAfterSeconds()).isPositive();
	}

	private static RateLimitProperties propertiesWithDefaultCapacity(int capacity) {
		RateLimitProperties properties = new RateLimitProperties();
		properties.setEnabled(true);
		properties.setTrustProxyHeaders(true);
		properties.setDefaultPolicy(policy("default", capacity, "/**"));
		properties.setCleanupInterval(Duration.ofMinutes(5));
		properties.setBucketTtl(Duration.ofMinutes(10));
		return properties;
	}

	private static RateLimitProperties.Policy policy(String name, int capacity, String pathPattern, String... methods) {
		RateLimitProperties.Policy policy = new RateLimitProperties.Policy();
		policy.setName(name);
		policy.setCapacity(capacity);
		policy.setRefillTokens(capacity);
		policy.setRefillPeriod(Duration.ofHours(1));
		policy.setPathPatterns(new ArrayList<>(List.of(pathPattern)));
		policy.setMethods(new LinkedHashSet<>(Set.of(methods)));
		return policy;
	}

	private static MockHttpServletRequest request(String method, String path, String remoteAddress) {
		MockHttpServletRequest request = new MockHttpServletRequest(method, path);
		request.setRemoteAddr(remoteAddress);
		return request;
	}

	@Test
	void separatesBucketsByAuthorizationToken() {
		RateLimitProperties properties = propertiesWithDefaultCapacity(1);
		RateLimitService service = new RateLimitService(properties);

		MockHttpServletRequest firstTokenRequest = request("GET", "/product", "10.0.0.10");
		firstTokenRequest.addHeader("Authorization", "Bearer token-one");
		MockHttpServletRequest secondTokenRequest = request("GET", "/product", "10.0.0.10");
		secondTokenRequest.addHeader("Authorization", "Bearer token-two");

		assertThat(service.consume(firstTokenRequest).allowed()).isTrue();
		assertThat(service.consume(firstTokenRequest).allowed()).isFalse();
		assertThat(service.consume(secondTokenRequest).allowed()).isTrue();
	}

	@Test
	void skipsExcludedPaths() {
		RateLimitProperties properties = propertiesWithDefaultCapacity(1);
		properties.setExcludedPaths(new ArrayList<>(List.of("/actuator/**")));
		RateLimitService service = new RateLimitService(properties);

		RateLimitService.RateLimitDecision decision = service.consume(request("GET", "/actuator/health", "10.0.0.10"));

		assertThat(decision.applicable()).isFalse();
		assertThat(decision.allowed()).isTrue();
	}
}
