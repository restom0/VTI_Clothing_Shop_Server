package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class OnSaleProductControllerIntegrationTest extends ControllerIntegrationTestSupport {

	static Stream<ApiEndpoint> endpoints() {
		return Stream.of(
				endpoint("GET /on-sale-product", HttpMethod.GET, "/on-sale-product", null, 200, Auth.NONE, false, false),
				endpoint("GET /on-sale-product/{id}", HttpMethod.GET, "/on-sale-product/1", null, 200, Auth.NONE, false, false)
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("endpoints")
	void shouldReachOnSaleProductEndpoints(ApiEndpoint endpoint) throws Exception {
		shouldReach(endpoint);
	}
}
