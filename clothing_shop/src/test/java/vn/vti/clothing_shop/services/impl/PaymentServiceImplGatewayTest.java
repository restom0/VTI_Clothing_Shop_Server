package vn.vti.clothing_shop.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;
import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.dtos.outs.ImportedProductDTO;
import vn.vti.clothing_shop.dtos.outs.OnSaleProductDTO;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.dtos.outs.OrderItemDTO;
import vn.vti.clothing_shop.dtos.outs.PaymentCheckoutResponse;
import vn.vti.clothing_shop.dtos.outs.ProductDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceImplGatewayTest {
	private HttpServer server;

	@AfterEach
	void stopServer() {
		if (server != null) {
			server.stop(0);
		}
	}

	@Test
	void payOsCheckoutBuildsPaymentDataAndReturnsGatewayResponse() throws Exception {
		PayOS payOS = mock(PayOS.class);
		when(payOS.createPaymentLink(any(PaymentData.class))).thenReturn(
				CheckoutResponseData.builder()
				                    .bin("970422")
				                    .accountNumber("12345678")
				                    .accountName("VTI SHOP")
				                    .amount(1000)
				                    .description("Order 555")
				                    .orderCode(555L)
				                    .currency("VND")
				                    .paymentLinkId("pay-link-id")
				                    .status("PENDING")
				                    .expiredAt(1L)
				                    .checkoutUrl("https://payos.example/checkout")
				                    .qrCode("qr")
				                    .build());
		PaymentServiceImpl service = service(payOS);

		PaymentCheckoutResponse response = service.createCheckout(order(PaymentMethod.PAYOS));

		assertThat(response.paymentMethod()).isEqualTo(PaymentMethod.PAYOS);
		assertThat(response.checkoutUrl()).isEqualTo("https://payos.example/checkout");
		assertThat(response.status()).isEqualTo("PENDING");
		ArgumentCaptor<PaymentData> captor = ArgumentCaptor.forClass(PaymentData.class);
		verify(payOS).createPaymentLink(captor.capture());
		assertThat(captor.getValue().getOrderCode()).isEqualTo(555L);
		assertThat(captor.getValue().getAmount()).isEqualTo(1000);
		assertThat(captor.getValue().getItems()).hasSize(2);
	}

	@Test
	void stripeAndZaloPayUseConfiguredHttpGateways() throws Exception {
		AtomicReference<String> stripeBody = new AtomicReference<>();
		AtomicReference<String> zaloBody = new AtomicReference<>();
		startServer(Map.of(
				"/stripe", exchange -> {
					stripeBody.set(readBody(exchange));
					assertThat(exchange.getRequestHeaders().getFirst("Authorization")).isEqualTo("Bearer sk_test");
					respond(exchange, 200, "{\"url\":\"https://stripe.example/session\",\"status\":\"open\"}");
				},
				"/zalo", exchange -> {
					zaloBody.set(readBody(exchange));
					respond(exchange, 200, "{\"order_url\":\"https://zalo.example/order\",\"return_message\":\"ok\"}");
				}
		));
		PaymentServiceImpl service = service(mock(PayOS.class));
		setField(service, "stripeSecretKey", "sk_test");
		setField(service, "stripeCheckoutSessionUrl", url("/stripe"));
		setField(service, "zaloPayAppId", "2553");
		setField(service, "zaloPayKey1", "zalo-secret");
		setField(service, "zaloPayCreateOrderUrl", url("/zalo"));
		setField(service, "zaloPayAppUser", "clothing-shop-test");

		PaymentCheckoutResponse stripe = service.createCheckout(order(PaymentMethod.STRIPE));
		PaymentCheckoutResponse zalo = service.createCheckout(order(PaymentMethod.ZALO_PAY));

		assertThat(stripe.checkoutUrl()).isEqualTo("https://stripe.example/session");
		assertThat(stripe.status()).isEqualTo("open");
		assertThat(stripe.providerData()).isEqualTo(Map.of("url", "https://stripe.example/session", "status", "open"));
		assertThat(stripeBody.get()).contains("client_reference_id=555", "line_items%5B0%5D%5Bquantity%5D=1");
		assertThat(zalo.checkoutUrl()).isEqualTo("https://zalo.example/order");
		assertThat(zalo.status()).isEqualTo("ok");
		assertThat(zaloBody.get()).contains("app_id=2553", "amount=1000", "mac=");
	}

	@Test
	void gatewayConfigurationAndGatewayFailuresAreWrapped() throws Exception {
		PaymentServiceImpl service = service(mock(PayOS.class));
		setField(service, "stripeSecretKey", "");
		OrderDTO stripeOrder = order(PaymentMethod.STRIPE);

		assertThatThrownBy(() -> service.createCheckout(stripeOrder))
				.isInstanceOf(WrapperException.class)
				.extracting("message")
				.isEqualTo("messages.payments.gatewayNotConfigured");

		PayOS payOS = mock(PayOS.class);
		when(payOS.createPaymentLink(any(PaymentData.class))).thenThrow(new IllegalStateException("down"));
		PaymentServiceImpl payOsService = service(payOS);
		assertThatThrownBy(() -> payOsService.createCheckout(order(PaymentMethod.PAYOS)))
				.isInstanceOf(WrapperException.class)
				.extracting("message")
				.isEqualTo("messages.payments.gatewayUnavailable");

		startServer(Map.of("/bad", exchange -> respond(exchange, 500, "{}")));
		setField(service, "stripeSecretKey", "sk_test");
		setField(service, "stripeCheckoutSessionUrl", url("/bad"));
		assertThatThrownBy(() -> service.createCheckout(stripeOrder))
				.isInstanceOf(WrapperException.class)
				.extracting("message")
				.isEqualTo("messages.payments.gatewayUnavailable");
	}

	@Test
	void successfulGatewayResponsesWithoutCheckoutUrlsAreWrapped() throws Exception {
		startServer(Map.of(
				"/stripe", exchange -> respond(exchange, 200, "{\"status\":\"open\"}"),
				"/zalo", exchange -> respond(exchange, 200, "{\"return_message\":\"ok\"}")
		));
		PaymentServiceImpl service = service(mock(PayOS.class));
		setField(service, "stripeSecretKey", "sk_test");
		setField(service, "stripeCheckoutSessionUrl", url("/stripe"));
		setField(service, "zaloPayAppId", "2553");
		setField(service, "zaloPayKey1", "zalo-secret");
		setField(service, "zaloPayCreateOrderUrl", url("/zalo"));

		assertThatThrownBy(() -> service.createCheckout(order(PaymentMethod.STRIPE)))
				.isInstanceOf(WrapperException.class)
				.extracting("message")
				.isEqualTo("messages.payments.gatewayUnavailable");
		assertThatThrownBy(() -> service.createCheckout(order(PaymentMethod.ZALO_PAY)))
				.isInstanceOf(WrapperException.class)
				.extracting("message")
				.isEqualTo("messages.payments.gatewayUnavailable");
	}

	private PaymentServiceImpl service(PayOS payOS) {
		PaymentServiceImpl service = new PaymentServiceImpl(payOS, new ObjectMapper());
		setField(service, "returnUrl", "https://client.example/return");
		setField(service, "cancelUrl", "https://client.example/cancel");
		setField(service, "currency", "VND");
		return service;
	}

	private static OrderDTO order(PaymentMethod paymentMethod) {
		return new OrderDTO(7L, "Street", "0912345678", "Ada", false, 1000L, 555L, null, paymentMethod, null,
		                    List.of(namedItem(), new OrderItemDTO(12L, null, 1)));
	}

	private static OrderItemDTO namedItem() {
		ProductDTO product = new ProductDTO(1L, "Sneaker", "Light", null, null);
		ImportedProductDTO importedProduct = new ImportedProductDTO();
		importedProduct.setProduct(product);
		OnSaleProductDTO onSaleProduct = new OnSaleProductDTO();
		onSaleProduct.setProduct(importedProduct);
		onSaleProduct.setSalePrice(500F);
		return new OrderItemDTO(11L, onSaleProduct, 2);
	}

	private void startServer(Map<String, ThrowingHandler> handlers) throws IOException {
		server = HttpServer.create(new InetSocketAddress(0), 0);
		handlers.forEach((path, handler) -> server.createContext(path, exchange -> {
			try {
				handler.handle(exchange);
			} catch (Exception exception) {
				respond(exchange, 500, "{}");
			}
		}));
		server.start();
	}

	private String url(String path) {
		return "http://127.0.0.1:" + server.getAddress().getPort() + path;
	}

	private static String readBody(HttpExchange exchange) throws IOException {
		return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
	}

	private static void respond(HttpExchange exchange, int status, String body) throws IOException {
		byte[] response = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(status, response.length);
		exchange.getResponseBody().write(response);
		exchange.close();
	}

	private static void setField(PaymentServiceImpl service, String field, Object value) {
		ReflectionTestUtils.setField(service, field, value);
	}

	@FunctionalInterface
	private interface ThrowingHandler {
		void handle(HttpExchange exchange) throws Exception;
	}
}
