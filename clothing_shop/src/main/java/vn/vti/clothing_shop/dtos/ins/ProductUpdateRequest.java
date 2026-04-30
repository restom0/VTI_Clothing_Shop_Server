package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductUpdateRequest(
		@NotBlank(message = "{messages.validation.required}")
		String name,

		String shortDescription,

		@NotNull(message = "{messages.validation.required}")
		@Positive
		Long categoryId,

		@NotNull(message = "{messages.validation.required}")
		@Positive
		Long brandId,

		@NotNull
		Long version
) {

}
