package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductCreateRequest(
        @NotBlank(message = "Name is required")
        String name,
        String shortDescription,

        @NotNull(message = "Category is required")
        @Positive
        Long categoryId,

        @NotNull(message = "Brand is required")
        @Positive
        Long brandId
) {


}
