package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemCreateRequest(
        @NotNull
        @Positive
        Long productId,

        @NotNull
        @Positive
        Long orderId,

        @NotNull
        @Positive
        Integer quantity
) {

}
