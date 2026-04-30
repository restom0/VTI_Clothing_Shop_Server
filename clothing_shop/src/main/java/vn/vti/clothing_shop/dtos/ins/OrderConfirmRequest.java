package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderConfirmRequest(
        @NotNull(message = "{messages.validation.required}")
        @Positive
        Long orderCode,

        @NotNull(message = "{messages.validation.required}")
        Boolean status
) {
}
