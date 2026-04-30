package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class CategoryControllerIntegrationTest extends ControllerIntegrationTestSupport {

	static Stream<ApiEndpoint> protectedEndpoints() {
		return endpoints().filter(ApiEndpoint::protectedEndpoint);
	}

	static Stream<ApiEndpoint> endpoints() {
		return Stream.of(
				endpoint("GET /categories", HttpMethod.GET, "/categories", null, 200, Auth.NONE, false, false),
				endpoint("POST /categories", HttpMethod.POST, "/categories", categoryCreateJson(), 201, Auth.USER, true, false),
				endpoint("PUT /categories/{id}", HttpMethod.PUT, "/categories/1", categoryUpdateJson(), 200, Auth.USER, true,
				         false),
				endpoint("DELETE /categories/{id}", HttpMethod.DELETE, "/categories/1", null, 200, Auth.USER, true, false),
				endpoint("GET /categories/{id}", HttpMethod.GET, "/categories/1", null, 200, Auth.NONE, false, false)
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("endpoints")
	void shouldReachCategoryEndpoints(ApiEndpoint endpoint) throws Exception {
		shouldReach(endpoint);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("protectedEndpoints")
	void shouldRejectProtectedCategoryEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
		shouldRejectWithoutToken(endpoint);
	}
}
