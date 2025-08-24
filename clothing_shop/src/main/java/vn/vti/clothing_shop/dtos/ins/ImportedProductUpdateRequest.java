package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import vn.vti.clothing_shop.constants.ClothGender;
import vn.vti.clothing_shop.validators.HexCode;

public record ImportedProductUpdateRequest(
        @NotNull(message = "Product ID is required")
        @Positive
        Long productId,

        @NotNull(message = "Color ID is required")
        @Positive
        Long colorId,

        @NotNull(message = "Size ID is required")
        @Positive
        Long sizeId,

        @NotNull(message = "Material ID is required")
        @Positive
        Long materialId,

        @NotBlank(message = "Color code is required")
        @HexCode
        String code,

        @NotBlank(message = "Color name is required")
        @Size(max = 255, message = "Color name must be less than 255 characters")
        String name,

        @NotBlank(message = "Size is required")
        @Size(max = 255, message = "Size must be less than 255 characters")
        String size,

        @NotBlank(message = "Height is required")
        @Size(max = 255, message = "Height must be less than 255 characters")
        String height,

        @NotBlank(message = "Weight is required")
        @Size(max = 255, message = "Weight must be less than 255 characters")
        String weight,

        @NotBlank(message = "Material is required")
        @Size(max = 255, message = "Material name must be less than 255 characters")
        String material,

        @NotNull(message = "Gender is required")
        ClothGender gender,

        @NotNull(message = "Import price is required")
        @Positive
        Integer importPrice,

        @NotNull(message = "Stock is required")
        @Positive
        Integer importNumber,

        @NotBlank(message = "Image URL is required")
        @Size(max = 255, message = "Image URL must be less than 255 characters")
        String imageUrl,

        @NotBlank(message = "Slider_1 is required")
        @Size(max = 255, message = "Slider_1 URL must be less than 255 characters")
        String sliderUrl1,

        @NotBlank(message = "Slider_2 is required")
        @Size(max = 255, message = "Slider_2 URL must be less than 255 characters")
        String sliderUrl2,

        @NotBlank(message = "Slider_3 is required")
        @Size(max = 255, message = "Slider_3 URL must be less than 255 characters")
        String sliderUrl3,

        @NotBlank(message = "Slider_4 is required")
        @Size(max = 255, message = "Slider_4 URL must be less than 255 characters")
        String sliderUrl4,

        @NotBlank(message = "Public ID URL is required")
        @Size(max = 255, message = "Public ID URL must be less than 255 characters")
        String publicIdUrl,

        @NotBlank(message = "Public ID Slider_1 is required")
        @Size(max = 255, message = "Public ID Slider_1 must be less than 255 characters")
        String publicIdSliderUrl1,

        @NotBlank(message = "Public ID Slider_2 is required")
        @Size(max = 255, message = "Public ID Slider_2 must be less than 255 characters")
        String publicIdSliderUrl2,

        @NotBlank(message = "Public ID Slider_3 is required")
        @Size(max = 255, message = "Public ID Slider_3 must be less than 255 characters")
        String publicIdSliderUrl3,

        @NotBlank(message = "Public ID Slider_4 is required")
        @Size(max = 255, message = "Public ID Slider_4 must be less than 255 characters")
        String publicIdSliderUrl4,

        @NotNull
        Long version
) {

}
