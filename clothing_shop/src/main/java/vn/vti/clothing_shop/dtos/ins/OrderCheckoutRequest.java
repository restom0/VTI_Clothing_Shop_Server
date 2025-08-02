package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderCheckoutRequest(
        @NotNull(message = "Order id is required")
        @Min(value = 1, message = "Order id must be greater than 0")
        Long orderId
) {
}
