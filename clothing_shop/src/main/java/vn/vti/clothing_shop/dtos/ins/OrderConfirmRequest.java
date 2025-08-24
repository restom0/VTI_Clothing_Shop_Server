package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderConfirmRequest(
        @NotNull(message = "Order id is required")
        @Positive
        Long orderCode,

        @NotNull(message = "Status is required")
        Boolean status
) {
}
