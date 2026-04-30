package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class OrderItemControllerIntegrationTest extends ControllerIntegrationTestSupport {

    @ParameterizedTest(name = "{0}")
    @MethodSource("endpoints")
    void shouldReachOrderItemEndpoints(ApiEndpoint endpoint) throws Exception {
        shouldReach(endpoint);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("protectedEndpoints")
    void shouldRejectProtectedOrderItemEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
        shouldRejectWithoutToken(endpoint);
    }

    static Stream<ApiEndpoint> endpoints() {
        return Stream.of(
                endpoint("GET /order-items/", HttpMethod.GET, "/order-items/", null, 200, Auth.USER, true, false),
                endpoint("GET /order-items/{orderId}", HttpMethod.GET, "/order-items/1", null, 200, Auth.USER, true, false),
                endpoint("GET /order-items/{orderId}/{id}", HttpMethod.GET, "/order-items/1/1", null, 200, Auth.USER, true, false),
                endpoint("POST /order-items/", HttpMethod.POST, "/order-items/", orderItemCreateJson(), 201, Auth.USER, true, false),
                endpoint("PUT /order-items/{orderId}/{id}", HttpMethod.PUT, "/order-items/1/1", orderItemUpdateJson(), 201, Auth.USER, true, false),
                endpoint("DELETE /order-items/{orderId}/{id}", HttpMethod.DELETE, "/order-items/1/1", null, 200, Auth.USER, true, false)
        );
    }

    static Stream<ApiEndpoint> protectedEndpoints() {
        return endpoints().filter(ApiEndpoint::protectedEndpoint);
    }
}
