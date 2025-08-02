package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vn.vti.clothing_shop.constants.ClothGender;
import vn.vti.clothing_shop.validators.HexCode;

public record ImportedProductUpdateRequest(
        @NotNull(message = "Product ID is required")
        @Min(value = 1, message = "Product ID must be greater than 0")
        Long productId,

        @NotNull(message = "Color ID is required")
        @Min(value = 1, message = "Color ID must be greater than 0")
        Long colorId,

        @NotNull(message = "Size ID is required")
        @Min(value = 1, message = "Size ID must be greater than 0")
        Long sizeId,

        @NotNull(message = "Material ID is required")
        @Min(value = 1, message = "Material ID must be greater than 0")
        Long materialId,

        @NotBlank(message = "Color code is required")
        @HexCode
        String code,

        @NotBlank(message = "Color name is required")
        String name,

        @NotBlank(message = "Size is required")
        String size,

        @NotBlank(message = "Height is required")
        String height,

        @NotBlank(message = "Weight is required")
        String weight,

        @NotBlank(message = "Material is required")
        String material,

        @NotNull(message = "Gender is required")
        ClothGender gender,

        @NotNull(message = "Import price is required")
        @Min(value = 0, message = "Import price must be positive")
        Integer importPrice,

        @NotNull(message = "Stock is required")
        @Min(value = 1, message = "Stock must be positive")
        Integer importNumber,

        @NotBlank(message = "Image URL is required")
        String imageUrl,

        @NotBlank(message = "Slider_1 is required")
        String sliderUrl1,

        @NotBlank(message = "Slider_2 is required")
        String sliderUrl2,

        @NotBlank(message = "Slider_3 is required")
        String sliderUrl3,

        @NotBlank(message = "Slider_4 is required")
        String sliderUrl4,

        @NotBlank(message = "Public ID URL is required")
        String publicIdUrl,

        @NotBlank(message = "Public ID Slider_1 is required")
        String publicIdSliderUrl1,

        @NotBlank(message = "Public ID Slider_2 is required")
        String publicIdSliderUrl2,

        @NotBlank(message = "Public ID Slider_3 is required")
        String publicIdSliderUrl3,

        @NotBlank(message = "Public ID Slider_4 is required")
        String publicIdSliderUrl4
) {

}
