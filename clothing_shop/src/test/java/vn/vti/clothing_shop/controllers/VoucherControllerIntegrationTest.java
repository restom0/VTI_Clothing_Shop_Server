package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class VoucherControllerIntegrationTest extends ControllerIntegrationTestSupport {

    @ParameterizedTest(name = "{0}")
    @MethodSource("endpoints")
    void shouldReachVoucherEndpoints(ApiEndpoint endpoint) throws Exception {
        shouldReach(endpoint);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("protectedEndpoints")
    void shouldRejectProtectedVoucherEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
        shouldRejectWithoutToken(endpoint);
    }

    static Stream<ApiEndpoint> endpoints() {
        return Stream.of(
                endpoint("GET /voucher/", HttpMethod.GET, "/voucher/", null, 200, Auth.NONE, false, false),
                endpoint("GET /voucher/{id}", HttpMethod.GET, "/voucher/1", null, 200, Auth.NONE, false, false),
                endpoint("GET /voucher/code/{code}", HttpMethod.GET, "/voucher/code/SAVE10", null, 200, Auth.NONE, false, false),
                endpoint("GET /voucher/available", HttpMethod.GET, "/voucher/available", null, 200, Auth.NONE, false, false),
                endpoint("POST /voucher/", HttpMethod.POST, "/voucher/", voucherCreateJson(), 200, Auth.USER, true, false),
                endpoint("PUT /voucher/{id}", HttpMethod.PUT, "/voucher/1", voucherUpdateJson(), 200, Auth.USER, true, false),
                endpoint("DELETE /voucher/{id}", HttpMethod.DELETE, "/voucher/1", null, 200, Auth.USER, true, false)
        );
    }

    static Stream<ApiEndpoint> protectedEndpoints() {
        return endpoints().filter(ApiEndpoint::protectedEndpoint);
    }
}
