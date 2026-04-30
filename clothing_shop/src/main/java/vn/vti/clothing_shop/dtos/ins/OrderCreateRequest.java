package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import vn.vti.clothing_shop.validators.PhoneNumber;

public record OrderCreateRequest(
		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String address,

		@PhoneNumber
		String phoneNumber,

		@NotBlank(message = "{messages.validation.required}")
		String receiverName
) {

}
