package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class ProductControllerIntegrationTest extends ControllerIntegrationTestSupport {

	static Stream<ApiEndpoint> protectedEndpoints() {
		return endpoints().filter(ApiEndpoint::protectedEndpoint);
	}

	static Stream<ApiEndpoint> endpoints() {
		return Stream.of(
				endpoint("GET /product/", HttpMethod.GET, "/product/", null, 200, Auth.NONE, false, false),
				endpoint("POST /product/", HttpMethod.POST, "/product/", productCreateJson(), 201, Auth.USER, true, false),
				endpoint("PUT /product/{id}", HttpMethod.PUT, "/product/1", productUpdateJson(), 200, Auth.USER, true, false),
				endpoint("DELETE /product/{id}", HttpMethod.DELETE, "/product/1", null, 200, Auth.USER, true, false),
				endpoint("GET /product/{id}", HttpMethod.GET, "/product/1", null, 200, Auth.NONE, false, false)
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("endpoints")
	void shouldReachProductEndpoints(ApiEndpoint endpoint) throws Exception {
		shouldReach(endpoint);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("protectedEndpoints")
	void shouldRejectProtectedProductEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
		shouldRejectWithoutToken(endpoint);
	}
}
