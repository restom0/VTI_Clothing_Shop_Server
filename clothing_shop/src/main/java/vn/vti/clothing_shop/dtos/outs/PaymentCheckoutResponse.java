package vn.vti.clothing_shop.dtos.outs;

import vn.vti.clothing_shop.constants.PaymentMethod;

/** Creates PaymentCheckoutResponse instance. */
public record PaymentCheckoutResponse(
		PaymentMethod paymentMethod,
		Long orderCode,
		Long amount,
		String currency,
		String status,
		String checkoutUrl,
		String returnUrl,
		String cancelUrl,
		Object providerData
) {
}
