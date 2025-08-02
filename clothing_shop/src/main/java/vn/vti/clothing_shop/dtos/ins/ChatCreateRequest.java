package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatCreateRequest(
        @NotBlank(message = "Vui lòng nhập tin nhắn")
        @Size(max = 255, message = "Nội dung tin nhắn không được vượt quá 255 ký tự")
        String content
) {
}
