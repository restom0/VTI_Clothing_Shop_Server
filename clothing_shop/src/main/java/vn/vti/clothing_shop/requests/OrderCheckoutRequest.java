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
@AllArgsConstructor
@NoArgsConstructor
public class OrderCheckoutRequest {
    @NotNull(message = "Order id is required")
    @Min(value = 1, message = "Order id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "Invalid Order id")
    private Long order_id;
}
