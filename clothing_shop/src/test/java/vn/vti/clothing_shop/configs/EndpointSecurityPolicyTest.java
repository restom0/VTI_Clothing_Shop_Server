package vn.vti.clothing_shop.configs;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EndpointSecurityPolicyTest {
	private final EndpointSecurityPolicy policy = new EndpointSecurityPolicy();

	@Test
	void publicCatalogReadsAndAuthEntryPointsDoNotRequireAuthentication() {
		assertThat(policy.requiresAuthentication("/product", "GET")).isFalse();
		assertThat(policy.requiresAuthentication("/brands/1", "GET")).isFalse();
		assertThat(policy.requiresAuthentication("/user/login", "POST")).isFalse();
		assertThat(policy.requiresAuthentication("/user/register", "POST")).isFalse();
	}

	@Test
	void commerceProfileAndCatalogWritesRequireAuthentication() {
		assertThat(policy.requiresAuthentication("/order", "GET")).isTrue();
		assertThat(policy.requiresAuthentication("/order-items/1", "DELETE")).isTrue();
		assertThat(policy.requiresAuthentication("/user/profile", "GET")).isTrue();
		assertThat(policy.requiresAuthentication("/product", "POST")).isTrue();
		assertThat(policy.requiresAuthentication("/brands/1", "PATCH")).isTrue();
		assertThat(policy.requiresAuthentication("/categories/1", "PUT")).isTrue();
	}

	@Test
	void adminEndpointsRequireAdminRole() {
		assertThat(policy.requiresAuthentication("/audit", "GET")).isTrue();
		assertThat(policy.requiresAdmin("/audit/42")).isTrue();
		assertThat(policy.requiresAdmin("/log")).isTrue();
		assertThat(policy.requiresAdmin("/order")).isFalse();
	}

	@Test
	void policyNormalizesCaseAndTrailingSlash() {
		assertThat(policy.requiresAuthentication("/Product/", "POST")).isTrue();
		assertThat(policy.requiresAdmin("/LOG/")).isTrue();
		assertThat(policy.normalize("")).isEqualTo("/");
	}
}
