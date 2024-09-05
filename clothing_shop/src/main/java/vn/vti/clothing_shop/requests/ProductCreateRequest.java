package vn.vti.clothing_shop.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String short_description;

    @NotNull(message = "Category is required")
    @Min(value = 0 , message = "Category must be positive")
    @Max(value = Long.MAX_VALUE, message = "Category is too large")
    private Long category_id;

    @NotNull(message = "Brand is required")
    @Min(value = 0 , message = "Brand must be positive")
    @Max(value = Long.MAX_VALUE, message = "Brand is too large")
    private Long brand_id;

}
