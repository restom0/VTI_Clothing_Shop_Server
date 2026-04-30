package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryUpdateRequest(
		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String name,

		@Size(max = 255, message = "{messages.validation.max255}")
		String description,

		@NotNull
		Long version
) {

}
