package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Creates OrderCheckoutRequest instance. */
public record OrderCheckoutRequest(
		@NotNull(message = "{messages.validation.required}")
		@Positive
		Long orderId
) {
}
