package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
        @NotNull(message = "Vui lòng chọn sản phẩm")
        @Positive
        Long productId,

        @NotBlank(message = "Vui lòng nhập nội dung")
        @Size(max = 255, message = "Nội dung không được vượt quá 255 ký tự")
        String content,

        @PositiveOrZero
        @DecimalMax(value = "5.0", message = "Số sao không hợp lệ")
        @NotNull(message = "Vui lòng nhập đánh giá")
        Float star
) {
}
