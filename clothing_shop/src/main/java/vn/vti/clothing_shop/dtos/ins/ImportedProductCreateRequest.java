package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import vn.vti.clothing_shop.constants.ClothGender;
import vn.vti.clothing_shop.validators.HexCode;

public record ImportedProductCreateRequest(
        @NotNull(message = "{messages.validation.required}")
        @Positive
        Long productId,

        @NotBlank(message = "{messages.validation.required}")
        @HexCode
        String code,

        @NotBlank(message = "{messages.validation.required}")
        String name,

        @NotBlank(message = "{messages.validation.required}")
        String size,

        @NotBlank(message = "{messages.validation.required}")
        String height,

        @NotBlank(message = "{messages.validation.required}")
        String weight,

        @NotBlank(message = "{messages.validation.required}")
        String material,

        @NotNull(message = "{messages.validation.required}")
        ClothGender gender,

        @NotNull(message = "{messages.validation.required}")
        @Positive
        Integer importPrice,

        @NotBlank(message = "{messages.validation.required}")
        String imageUrl,

        @NotBlank(message = "{messages.validation.required}")
        String sliderUrl1,

        @NotBlank(message = "{messages.validation.required}")
        String sliderUrl2,

        @NotBlank(message = "{messages.validation.required}")
        String sliderUrl3,

        @NotBlank(message = "{messages.validation.required}")
        String sliderUrl4,

        @NotBlank(message = "{messages.validation.required}")
        String publicIdUrl,

        @NotBlank(message = "{messages.validation.required}")
        String publicIdSliderUrl1,

        @NotBlank(message = "{messages.validation.required}")
        String publicIdSliderUrl2,

        @NotBlank(message = "{messages.validation.required}")
        String publicIdSliderUrl3,

        @NotBlank(message = "{messages.validation.required}")
        String publicIdSliderUrl4,

        @NotNull(message = "{messages.validation.required}")
        @Positive
        Integer importNumber
) {

}
