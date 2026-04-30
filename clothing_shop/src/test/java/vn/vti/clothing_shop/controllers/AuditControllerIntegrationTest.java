package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class AuditControllerIntegrationTest extends ControllerIntegrationTestSupport {

    @ParameterizedTest(name = "{0}")
    @MethodSource("endpoints")
    void shouldReachAuditEndpoints(ApiEndpoint endpoint) throws Exception {
        shouldReach(endpoint);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("endpoints")
    void shouldRejectAuditEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
        shouldRejectWithoutToken(endpoint);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("endpoints")
    void shouldRejectAuditEndpointsForRegularUser(ApiEndpoint endpoint) throws Exception {
        shouldRejectUser(endpoint);
    }

    static Stream<ApiEndpoint> endpoints() {
        return Stream.of(
                endpoint("GET /audit/", HttpMethod.GET, "/audit/", null, 200, Auth.ADMIN, true, true)
        );
    }
}
