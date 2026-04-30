package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import vn.vti.clothing_shop.constants.ClothGender;
import vn.vti.clothing_shop.validators.HexCode;

public record ImportedProductUpdateRequest(
		@NotNull(message = "{messages.validation.required}")
		@Positive
		Long productId,

		@NotNull(message = "{messages.validation.required}")
		@Positive
		Long colorId,

		@NotNull(message = "{messages.validation.required}")
		@Positive
		Long sizeId,

		@NotNull(message = "{messages.validation.required}")
		@Positive
		Long materialId,

		@NotBlank(message = "{messages.validation.required}")
		@HexCode
		String code,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String name,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String size,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String height,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String weight,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String material,

		@NotNull(message = "{messages.validation.required}")
		ClothGender gender,

		@NotNull(message = "{messages.validation.required}")
		@Positive
		Integer importPrice,

		@NotNull(message = "{messages.validation.required}")
		@Positive
		Integer importNumber,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String imageUrl,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String sliderUrl1,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String sliderUrl2,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String sliderUrl3,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String sliderUrl4,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String publicIdUrl,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String publicIdSliderUrl1,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String publicIdSliderUrl2,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String publicIdSliderUrl3,

		@NotBlank(message = "{messages.validation.required}")
		@Size(max = 255, message = "{messages.validation.max255}")
		String publicIdSliderUrl4,

		@NotNull
		Long version
) {

}
