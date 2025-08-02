package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
        @NotNull(message = "Vui lòng chọn sản phẩm")
        @Min(value = 1, message = "ID sản phẩm phải lớn hơn 0")
        Long productId,

        @NotBlank(message = "Vui lòng nhập nội dung")
        @Size(max = 255, message = "Nội dung không được vượt quá 255 ký tự")
        String content,

        @DecimalMin(value = "0.0", message = "Số sao không hợp lệ")
        @DecimalMax(value = "5.0", message = "Số sao không hợp lệ")
        @NotNull(message = "Vui lòng nhập đánh giá")
        Float star,

        @NotNull
        Long version
) {
}
