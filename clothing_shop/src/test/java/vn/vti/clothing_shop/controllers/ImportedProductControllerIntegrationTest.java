package vn.vti.clothing_shop.controllers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;

import java.util.stream.Stream;

class ImportedProductControllerIntegrationTest extends ControllerIntegrationTestSupport {

    @ParameterizedTest(name = "{0}")
    @MethodSource("endpoints")
    void shouldReachImportedProductEndpoints(ApiEndpoint endpoint) throws Exception {
        shouldReach(endpoint);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("protectedEndpoints")
    void shouldRejectProtectedImportedProductEndpointsWithoutToken(ApiEndpoint endpoint) throws Exception {
        shouldRejectWithoutToken(endpoint);
    }

    static Stream<ApiEndpoint> endpoints() {
        return Stream.of(
                endpoint("GET /imported-product/", HttpMethod.GET, "/imported-product/", null, 200, Auth.NONE, false, false),
                endpoint("POST /imported-product/", HttpMethod.POST, "/imported-product/", importedProductCreateJson(), 201, Auth.USER, true, false),
                endpoint("PUT /imported-product/{id}", HttpMethod.PUT, "/imported-product/1", importedProductUpdateJson(), 200, Auth.USER, true, false),
                endpoint("DELETE /imported-product/{id}", HttpMethod.DELETE, "/imported-product/1", null, 200, Auth.USER, true, false),
                endpoint("GET /imported-product/{filter}/{id}", HttpMethod.GET, "/imported-product/PRODUCT/1", null, 200, Auth.NONE, false, false),
                endpoint("GET /imported-product/colors", HttpMethod.GET, "/imported-product/colors", null, 200, Auth.NONE, false, false),
                endpoint("GET /imported-product/materials", HttpMethod.GET, "/imported-product/materials", null, 200, Auth.NONE, false, false),
                endpoint("GET /imported-product/sizes", HttpMethod.GET, "/imported-product/sizes", null, 200, Auth.NONE, false, false)
        );
    }

    static Stream<ApiEndpoint> protectedEndpoints() {
        return endpoints().filter(ApiEndpoint::protectedEndpoint);
    }
}
