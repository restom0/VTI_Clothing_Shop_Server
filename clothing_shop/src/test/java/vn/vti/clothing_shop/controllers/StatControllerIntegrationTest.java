package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class StatControllerIntegrationTest extends ControllerIntegrationTestSupport {

	static Stream<ApiEndpoint> endpoints() {
		return Stream.of(
				endpoint("GET /stat/analysis", HttpMethod.GET, "/stat/analysis", null, 200, Auth.NONE, false, false)
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("endpoints")
	void shouldReachStatEndpoints(ApiEndpoint endpoint) throws Exception {
		shouldReach(endpoint);
	}
}
