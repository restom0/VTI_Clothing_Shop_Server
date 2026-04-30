package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemUpdateRequest(
		@NotNull(message = "{messages.validation.required}")
		@Positive
		Long productId,

		@NotNull(message = "{messages.validation.required}")
		@Positive
		Integer quantity,

		@NotNull
		@Positive
		Long version
) {

}
