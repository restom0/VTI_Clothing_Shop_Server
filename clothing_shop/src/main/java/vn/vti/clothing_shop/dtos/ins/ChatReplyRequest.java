package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatReplyRequest(
        @NotBlank(message = "Vui lòng nhập phản hồi")
        String reply,

        @NotNull
        Long version
) {
}
