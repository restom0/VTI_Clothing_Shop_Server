package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BrandUpdateRequest(
        @NotBlank(message = "Vui lòng nhập tên thương hiệu")
        @Size(max = 255, message = "Tên thương hiệu không được vượt quá 255 ký tự")
        String name,

        @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
        String description,

        @NotNull
        Long version
) {

}
