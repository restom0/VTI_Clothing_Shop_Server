package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class UserControllerIntegrationTest extends ControllerIntegrationTestSupport {

	static Stream<ApiEndpoint> protectedEndpoints() {
		return endpoints().filter(ApiEndpoint::protectedEndpoint);
	}

	static Stream<ApiEndpoint> endpoints() {
		return Stream.of(
				endpoint("POST /user/login", HttpMethod.POST, "/user/login", userLoginJson(), 200, Auth.NONE, false, false),
				endpoint("POST /user/register", HttpMethod.POST, "/user/register", userCreateJson(), 201, Auth.NONE, false,
				         false),
				endpoint("GET /user/profile", HttpMethod.GET, "/user/profile", null, 200, Auth.USER, true, false),
				endpoint("GET /user/", HttpMethod.GET, "/user/", null, 200, Auth.NONE, false, false),
				endpoint("PUT /user/", HttpMethod.PUT, "/user/", userUpdateJson(), 200, Auth.USER, true, false),
				endpoint("PUT /user/password", HttpMethod.PUT, "/user/password", userPasswordJson(), 200, Auth.USER, true, false),
				endpoint("DELETE /user/{id}", HttpMethod.DELETE, "/user/1", null, 200, Auth.USER, true, false),
				endpoint("DELETE /user/", HttpMethod.DELETE, "/user/", null, 200, Auth.USER, true, false)
		);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("endpoints")
	void shouldReachUserEndpoints(ApiEndpoint endpoint) throws Exception {
		shouldReach(endpoint);
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("protectedEndpoints")
	void shouldRejectProtectedUserEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
		shouldRejectWithoutToken(endpoint);
	}
}
