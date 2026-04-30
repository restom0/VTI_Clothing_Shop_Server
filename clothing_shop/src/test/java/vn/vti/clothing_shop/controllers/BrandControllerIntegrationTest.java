package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class BrandControllerIntegrationTest extends ControllerIntegrationTestSupport {

    @ParameterizedTest(name = "{0}")
    @MethodSource("endpoints")
    void shouldReachBrandEndpoints(ApiEndpoint endpoint) throws Exception {
        shouldReach(endpoint);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("protectedEndpoints")
    void shouldRejectProtectedBrandEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
        shouldRejectWithoutToken(endpoint);
    }

    static Stream<ApiEndpoint> endpoints() {
        return Stream.of(
                endpoint("GET /brands/", HttpMethod.GET, "/brands/", null, 200, Auth.NONE, false, false),
                endpoint("POST /brands/brand", HttpMethod.POST, "/brands/brand", brandCreateJson(), 201, Auth.USER, true, false),
                endpoint("PATCH /brands/{id}", HttpMethod.PATCH, "/brands/1", brandUpdateJson(), 202, Auth.USER, true, false),
                endpoint("DELETE /brands/{id}", HttpMethod.DELETE, "/brands/1", null, 200, Auth.USER, true, false),
                endpoint("GET /brands/{id}", HttpMethod.GET, "/brands/1", null, 200, Auth.NONE, false, false)
        );
    }

    static Stream<ApiEndpoint> protectedEndpoints() {
        return endpoints().filter(ApiEndpoint::protectedEndpoint);
    }
}
