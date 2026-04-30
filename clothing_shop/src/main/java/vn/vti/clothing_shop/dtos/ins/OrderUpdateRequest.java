package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.validators.PhoneNumber;

public record OrderUpdateRequest(
		@NotBlank(message = "{messages.validation.required}")

		String address,

		@NotBlank(message = "{messages.validation.required}")
		@PhoneNumber
		String phoneNumber,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255)
		String receiverName,

		@NotNull(message = "{messages.validation.required}")
		Boolean isPresent,

		@NotNull(message = "{messages.validation.required}")
		PaymentMethod paymentMethod,

		Long voucherId
) {

}
