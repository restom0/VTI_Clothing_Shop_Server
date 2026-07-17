package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Creates ChatCreateRequest instance. */
public record ChatCreateRequest(
		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String content
) {
}
