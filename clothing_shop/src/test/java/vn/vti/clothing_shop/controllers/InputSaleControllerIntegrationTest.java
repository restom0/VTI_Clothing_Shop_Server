package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class InputSaleControllerIntegrationTest extends ControllerIntegrationTestSupport {

	static Stream<ApiEndpoint> protectedEndpoints() {
		return endpoints().filter(ApiEndpoint::protectedEndpoint);
	}

	static Stream<ApiEndpoint> endpoints() {
		return Stream.of(
				endpoint("GET /input-sale/", HttpMethod.GET, "/input-sale/", null, 200, Auth.NONE, false, false),
				endpoint("GET /input-sale/{id}", HttpMethod.GET, "/input-sale/1", null, 200, Auth.NONE, false, false),
				endpoint("POST /input-sale/", HttpMethod.POST, "/input-sale/", inputSaleCreateJson(), 201, Auth.USER, true,
				         false),
				endpoint("PUT /input-sale/{id}", HttpMethod.PUT, "/input-sale/1", inputSaleUpdateJson(), 200, Auth.USER, true,
				         false),
				endpoint("DELETE /input-sale/{id}", HttpMethod.DELETE, "/input-sale/1", null, 200, Auth.USER, true, false)
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("endpoints")
	void shouldReachInputSaleEndpoints(ApiEndpoint endpoint) throws Exception {
		shouldReach(endpoint);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("protectedEndpoints")
	void shouldRejectProtectedInputSaleEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
		shouldRejectWithoutToken(endpoint);
	}
}
