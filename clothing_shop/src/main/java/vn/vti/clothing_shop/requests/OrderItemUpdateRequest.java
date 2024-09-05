package vn.vti.clothing_shop.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemUpdateRequest {
    @NotNull(message = "Id is required")
    @Min(value = 1, message = "Id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "Id is too large")
    private Long id;

    @NotNull(message = "Order id is required")
    @Min(value = 1, message = "Order id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "Order id is too large")
    private Long order_id;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be greater than 0")
    private Integer quantity;
}
