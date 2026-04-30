package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class OrderControllerIntegrationTest extends ControllerIntegrationTestSupport {

    @ParameterizedTest(name = "{0}")
    @MethodSource("endpoints")
    void shouldReachOrderEndpoints(ApiEndpoint endpoint) throws Exception {
        shouldReach(endpoint);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("protectedEndpoints")
    void shouldRejectProtectedOrderEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
        shouldRejectWithoutToken(endpoint);
    }

    static Stream<ApiEndpoint> endpoints() {
        return Stream.of(
                endpoint("GET /order", HttpMethod.GET, "/order", null, 200, Auth.USER, true, false),
                endpoint("GET /order/user", HttpMethod.GET, "/order/user", null, 200, Auth.USER, true, false),
                endpoint("GET /order/cart", HttpMethod.GET, "/order/cart", null, 200, Auth.USER, true, false),
                endpoint("PUT /order/{id}", HttpMethod.PUT, "/order/1", orderUpdateJson(), 200, Auth.USER, true, false),
                endpoint("DELETE /order/{orderId}/{id}", HttpMethod.DELETE, "/order/1/1", null, 200, Auth.USER, true, false),
                endpoint("POST /order/checkout", HttpMethod.POST, "/order/checkout", orderCheckoutJson(), 200, Auth.USER, true, false),
                endpoint("PUT /order/success", HttpMethod.PUT, "/order/success", orderConfirmJson(), 200, Auth.USER, true, false),
                endpoint("PUT /order/cancel", HttpMethod.PUT, "/order/cancel", orderConfirmJson(), 200, Auth.USER, true, false)
        );
    }

    static Stream<ApiEndpoint> protectedEndpoints() {
        return endpoints().filter(ApiEndpoint::protectedEndpoint);
    }
}
