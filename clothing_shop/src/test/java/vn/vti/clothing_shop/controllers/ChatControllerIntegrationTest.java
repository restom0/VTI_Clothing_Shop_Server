package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class ChatControllerIntegrationTest extends ControllerIntegrationTestSupport {

	static Stream<ApiEndpoint> protectedEndpoints() {
		return endpoints().filter(ApiEndpoint::protectedEndpoint);
	}

	static Stream<ApiEndpoint> endpoints() {
		return Stream.of(
				endpoint("GET /chat/all", HttpMethod.GET, "/chat/all", null, 200, Auth.USER, true, false),
				endpoint("GET /chat/", HttpMethod.GET, "/chat/", null, 200, Auth.USER, true, false),
				endpoint("POST /chat/", HttpMethod.POST, "/chat/", chatCreateJson(), 201, Auth.USER, true, false),
				endpoint("PUT /chat/{id}", HttpMethod.PUT, "/chat/1", chatUpdateJson(), 200, Auth.USER, true, false),
				endpoint("DELETE /chat/{id}", HttpMethod.DELETE, "/chat/1", null, 200, Auth.USER, true, false),
				endpoint("PUT /chat/reply/{id}", HttpMethod.PUT, "/chat/reply/1", chatReplyJson(), 200, Auth.USER, true, false)
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("endpoints")
	void shouldReachChatEndpoints(ApiEndpoint endpoint) throws Exception {
		shouldReach(endpoint);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("protectedEndpoints")
	void shouldRejectProtectedChatEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
		shouldRejectWithoutToken(endpoint);
	}
}
