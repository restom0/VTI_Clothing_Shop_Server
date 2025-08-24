package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatReplyRequest(
        @NotBlank(message = "Vui lòng nhập phản hồi")
        @Size(max = 255, message = "Phản hồi không được vượt quá 255 ký tự")
        String reply
) {
}
