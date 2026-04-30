package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class CommentControllerIntegrationTest extends ControllerIntegrationTestSupport {

	static Stream<ApiEndpoint> protectedEndpoints() {
		return endpoints().filter(ApiEndpoint::protectedEndpoint);
	}

	static Stream<ApiEndpoint> endpoints() {
		return Stream.of(
				endpoint("GET /comment/", HttpMethod.GET, "/comment/", null, 200, Auth.NONE, false, false),
				endpoint("GET /comment/{id}", HttpMethod.GET, "/comment/1", null, 200, Auth.NONE, false, false),
				endpoint("POST /comment/", HttpMethod.POST, "/comment/", commentCreateJson(), 201, Auth.USER, true, false),
				endpoint("PUT /comment/{id}", HttpMethod.PUT, "/comment/1", commentUpdateJson(), 201, Auth.USER, true, false),
				endpoint("DELETE /comment/{id}", HttpMethod.DELETE, "/comment/1", null, 200, Auth.USER, true, false)
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("endpoints")
	void shouldReachCommentEndpoints(ApiEndpoint endpoint) throws Exception {
		shouldReach(endpoint);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("protectedEndpoints")
	void shouldRejectProtectedCommentEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
		shouldRejectWithoutToken(endpoint);
	}
}
