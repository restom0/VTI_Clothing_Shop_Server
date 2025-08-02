package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateRequest(
        @NotBlank(message = "Vui lòng nhập tên danh mục")
        @Size(max = 255, message = "Tên danh mục không được vượt quá 255 ký tự")
        String name,

        @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
        String description
) {

}
