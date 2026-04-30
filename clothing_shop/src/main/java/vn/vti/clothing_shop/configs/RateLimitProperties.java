package vn.vti.clothing_shop.configs;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "application.rate-limit")
public class RateLimitProperties {
	private boolean enabled = true;
	private boolean trustProxyHeaders = true;
	private String clientIpHeader = "X-Forwarded-For";
	private Duration cleanupInterval = Duration.ofMinutes(5);
	private Duration bucketTtl = Duration.ofMinutes(10);
	private Policy defaultPolicy = new Policy();
	private List<String> excludedPaths = new ArrayList<>(List.of(
			"/actuator/**",
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/webjars/**"
	));
	private List<Policy> routes = new ArrayList<>();

	@Data
	public static class Policy {
		private String name = "default";
		private List<String> pathPatterns = new ArrayList<>(List.of("/**"));
		private Set<String> methods = new LinkedHashSet<>();
		private int capacity = 120;
		private int refillTokens = 120;
		private Duration refillPeriod = Duration.ofMinutes(1);

		public boolean matchesMethod(String method) {
			return methods == null || methods.isEmpty() || methods.contains(method.toUpperCase());
		}
	}
}
