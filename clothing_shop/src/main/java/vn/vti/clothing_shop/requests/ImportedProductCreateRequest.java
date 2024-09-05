package vn.vti.clothing_shop.requests;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.ClothGender;

import static vn.vti.clothing_shop.constants.RegularExpression.COLOR;
import static vn.vti.clothing_shop.constants.RegularExpression.NUMBER;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportedProductCreateRequest {
    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "Product ID is too large")
    private Long product_id;

    @Pattern(regexp = COLOR, message = "Color ID must be a hex number")
    @NotBlank(message = "Color code is required")
    private String color_code;

    @NotBlank(message = "Color name is required")
    private String color_name;

    @NotBlank(message = "Size is required")
    private String size;

    @NotBlank(message = "Height is required")
    private String height;

    @NotBlank(message = "Weight is required")
    private String weight;

    @NotBlank(message = "Material is required")
    private String material;

    @NotNull(message = "Gender is required")
    private ClothGender gender;

    @NotNull(message = "Import price is required")
    @Min(value = 1, message = "Import price must be greater than 0")
    private Integer importPrice;

    @NotBlank(message = "Image URL is required")
    private String image_url;

    @NotBlank(message = "Slider_1 is required")
    private String slider_url_1;

    @NotBlank(message = "Slider_2 is required")
    private String slider_url_2;

    @NotBlank(message = "Slider_3 is required")
    private String slider_url_3;

    @NotBlank(message = "Slider_4 is required")
    private String slider_url_4;

    @NotBlank(message = "Public ID URL is required")
    private String public_id_url;

    @NotBlank(message = "Public ID Slider_1 is required")
    private String public_id_slider_url_1;

    @NotBlank(message = "Public ID Slider_2 is required")
    private String public_id_slider_url_2;

    @NotBlank(message = "Public ID Slider_3 is required")
    private String public_id_slider_url_3;

    @NotBlank(message = "Public ID Slider_4 is required")
    private String public_id_slider_url_4;

    @NotNull(message = "Stock is required")
    @Min(value = 1, message = "Stock must be positive")
    private Integer importNumber;
}
