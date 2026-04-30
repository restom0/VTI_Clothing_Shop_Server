package vn.vti.clothing_shop.services.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.dtos.outs.OrderItemDTO;
import vn.vti.clothing_shop.dtos.outs.PaymentCheckoutResponse;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.exceptions.BaseCheckedException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.services.interfaces.PaymentService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	private static final String MANUAL_STATUS = "MANUAL_CONFIRMATION_REQUIRED";
	private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
	private static final DateTimeFormatter ZALO_PAY_TRANSACTION_DATE = DateTimeFormatter.ofPattern("yyMMdd").withZone(
			VIETNAM_ZONE);

	private final PayOS payOS;
	private final ObjectMapper objectMapper;
	private final HttpClient httpClient = HttpClient.newHttpClient();

	@Value("${payment.return-url}")
	private String returnUrl;

	@Value("${payment.cancel-url}")
	private String cancelUrl;

	@Value("${payment.currency:VND}")
	private String currency;

	@Value("${stripe.secret-key:}")
	private String stripeSecretKey;

	@Value("${stripe.checkout-session-url:https://api.stripe.com/v1/checkout/sessions}")
	private String stripeCheckoutSessionUrl;

	@Value("${zalopay.app-id:}")
	private String zaloPayAppId;

	@Value("${zalopay.key1:}")
	private String zaloPayKey1;

	@Value("${zalopay.create-order-url:https://sb-openapi.zalopay.vn/v2/create}")
	private String zaloPayCreateOrderUrl;

	@Value("${zalopay.app-user:clothing-shop}")
	private String zaloPayAppUser;

	@Override
	public PaymentCheckoutResponse createCheckout(OrderDTO orderDTO) throws WrapperException {
		try {
			if (orderDTO == null) {
				throw new BadRequestException("messages.orders.notfound");
			}
			PaymentMethod paymentMethod = orderDTO.getPaymentMethod() == null ? PaymentMethod.COD : orderDTO.getPaymentMethod();
			return switch (paymentMethod) {
				case PAYOS -> createPayOsCheckout(orderDTO);
				case STRIPE -> createStripeCheckout(orderDTO);
				case ZALO_PAY -> createZaloPayCheckout(orderDTO);
				case COD, EBanking, MOMO -> createManualCheckout(orderDTO, paymentMethod);
			};
		} catch (BaseCheckedException e) {
			throw new WrapperException(e);
		} catch (Exception e) {
			throw new WrapperException(new BadRequestException("messages.payments.gatewayUnavailable"));
		}
	}

	private PaymentCheckoutResponse createManualCheckout(OrderDTO orderDTO, PaymentMethod paymentMethod) {
		Map<String, Object> providerData = new LinkedHashMap<>();
		providerData.put("manual", true);
		providerData.put("message", "Payment will be confirmed manually");
		return new PaymentCheckoutResponse(
				paymentMethod,
				orderDTO.getOrderCode(),
				orderDTO.getTotalPrice(),
				currency,
				MANUAL_STATUS,
				null,
				returnUrl,
				cancelUrl,
				providerData
		);
	}

	private PaymentCheckoutResponse createPayOsCheckout(OrderDTO orderDTO) throws Exception {
		PaymentData paymentData = PaymentData.builder()
		                                     .orderCode(orderDTO.getOrderCode())
		                                     .items(buildPayOsItems(orderDTO))
		                                     .amount(Math.toIntExact(orderDTO.getTotalPrice()))
		                                     .description("Order " + orderDTO.getOrderCode())
		                                     .returnUrl(returnUrl)
		                                     .cancelUrl(cancelUrl)
		                                     .build();
		CheckoutResponseData checkoutResponseData = payOS.createPaymentLink(paymentData);
		return new PaymentCheckoutResponse(
				PaymentMethod.PAYOS,
				orderDTO.getOrderCode(),
				orderDTO.getTotalPrice(),
				checkoutResponseData.getCurrency(),
				checkoutResponseData.getStatus(),
				checkoutResponseData.getCheckoutUrl(),
				returnUrl,
				cancelUrl,
				checkoutResponseData
		);
	}

	private PaymentCheckoutResponse createStripeCheckout(OrderDTO orderDTO)
			throws IOException, InterruptedException, BadRequestException {
		requireConfigured(stripeSecretKey);
		Map<String, String> form = new LinkedHashMap<>();
		form.put("mode", "payment");
		form.put("success_url", returnUrl);
		form.put("cancel_url", cancelUrl);
		form.put("client_reference_id", String.valueOf(orderDTO.getOrderCode()));
		form.put("line_items[0][price_data][currency]", currency.toLowerCase(Locale.ROOT));
		form.put("line_items[0][price_data][product_data][name]", "Order " + orderDTO.getOrderCode());
		form.put("line_items[0][price_data][unit_amount]", String.valueOf(orderDTO.getTotalPrice()));
		form.put("line_items[0][quantity]", "1");

		Map<String, Object> response = postForm(stripeCheckoutSessionUrl, form, "Bearer " + stripeSecretKey);
		String checkoutUrl = asString(response.get("url"));
		if (!StringUtils.hasText(checkoutUrl)) {
			throw new BadRequestException("messages.payments.gatewayUnavailable");
		}
		return new PaymentCheckoutResponse(
				PaymentMethod.STRIPE,
				orderDTO.getOrderCode(),
				orderDTO.getTotalPrice(),
				currency,
				asString(response.get("status")),
				checkoutUrl,
				returnUrl,
				cancelUrl,
				response
		);
	}

	private PaymentCheckoutResponse createZaloPayCheckout(OrderDTO orderDTO) throws Exception {
		requireConfigured(zaloPayAppId);
		requireConfigured(zaloPayKey1);

		long appTime = Instant.now().toEpochMilli();
		String appTransId = ZALO_PAY_TRANSACTION_DATE.format(Instant.now()) + "_" + orderDTO.getOrderCode();
		String embedData = objectMapper.writeValueAsString(Map.of(
				"redirecturl", returnUrl,
				"preferred_payment_method", List.of("zalopay_wallet")
		));
		String items = objectMapper.writeValueAsString(buildZaloPayItems(orderDTO));
		String macInput = String.join("|",
		                              zaloPayAppId,
		                              appTransId,
		                              zaloPayAppUser,
		                              String.valueOf(orderDTO.getTotalPrice()),
		                              String.valueOf(appTime),
		                              embedData,
		                              items
		);

		Map<String, String> form = new LinkedHashMap<>();
		form.put("app_id", zaloPayAppId);
		form.put("app_user", zaloPayAppUser);
		form.put("app_trans_id", appTransId);
		form.put("app_time", String.valueOf(appTime));
		form.put("amount", String.valueOf(orderDTO.getTotalPrice()));
		form.put("description", "VTI Clothing Shop - Order " + orderDTO.getOrderCode());
		form.put("item", items);
		form.put("embed_data", embedData);
		form.put("bank_code", "");
		form.put("mac", hmacSha256(zaloPayKey1, macInput));

		Map<String, Object> response = postForm(zaloPayCreateOrderUrl, form, null);
		String checkoutUrl = asString(response.get("order_url"));
		if (!StringUtils.hasText(checkoutUrl)) {
			throw new BadRequestException("messages.payments.gatewayUnavailable");
		}
		return new PaymentCheckoutResponse(
				PaymentMethod.ZALO_PAY,
				orderDTO.getOrderCode(),
				orderDTO.getTotalPrice(),
				currency,
				asString(response.get("return_message")),
				checkoutUrl,
				returnUrl,
				cancelUrl,
				response
		);
	}

	private List<ItemData> buildPayOsItems(OrderDTO orderDTO) {
		return orderItems(orderDTO).stream()
		                           .map(orderItem -> ItemData.builder()
		                                                     .name(resolveItemName(orderItem))
		                                                     .quantity(orderItem.getQuantity())
		                                                     .build())
		                           .toList();
	}

	private List<Map<String, Object>> buildZaloPayItems(OrderDTO orderDTO) {
		return orderItems(orderDTO).stream()
		                           .map(orderItem -> {
			                           Map<String, Object> item = new LinkedHashMap<>();
			                           item.put("itemid", orderItem.getId());
			                           item.put("itemname", resolveItemName(orderItem));
			                           item.put("itemquantity", orderItem.getQuantity());
			                           return item;
		                           })
		                           .toList();
	}

	private List<OrderItemDTO> orderItems(OrderDTO orderDTO) {
		return orderDTO.getOrderItems() == null ? List.of() : orderDTO.getOrderItems();
	}

	private String resolveItemName(OrderItemDTO orderItemDTO) {
		if (orderItemDTO == null
				|| orderItemDTO.getProduct() == null
				|| orderItemDTO.getProduct().getProduct() == null
				|| orderItemDTO.getProduct().getProduct().getProduct() == null
				|| !StringUtils.hasText(orderItemDTO.getProduct().getProduct().getProduct().getName())) {
			return "Order item";
		}
		return orderItemDTO.getProduct().getProduct().getProduct().getName();
	}

	private Map<String, Object> postForm(String url, Map<String, String> form, String authorization)
			throws IOException, InterruptedException, BadRequestException {
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
		                                         .header("Content-Type", "application/x-www-form-urlencoded")
		                                         .POST(HttpRequest.BodyPublishers.ofString(encodeForm(form)));
		if (StringUtils.hasText(authorization)) {
			builder.header("Authorization", authorization);
		}
		HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() < 200 || response.statusCode() >= 300) {
			throw new BadRequestException("messages.payments.gatewayUnavailable");
		}
		return objectMapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {
		});
	}

	private String encodeForm(Map<String, String> form) {
		List<String> pairs = new ArrayList<>();
		form.forEach((key, value) -> pairs.add(URLEncoder.encode(key, StandardCharsets.UTF_8)
				                                       + "="
				                                       + URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8)));
		return String.join("&", pairs);
	}

	private String hmacSha256(String key, String data) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
		byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
		StringBuilder hex = new StringBuilder(bytes.length * 2);
		for (byte current : bytes) {
			hex.append(String.format("%02x", current));
		}
		return hex.toString();
	}

	private void requireConfigured(String value) throws BadRequestException {
		if (!StringUtils.hasText(value)) {
			throw new BadRequestException("messages.payments.gatewayNotConfigured");
		}
	}

	private String asString(Object value) {
		return value == null ? null : String.valueOf(value);
	}
}
