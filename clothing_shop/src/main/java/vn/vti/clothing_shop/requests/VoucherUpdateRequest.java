package vn.vti.clothing_shop.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherUpdateRequest {
    @NotNull(message = "Code is required")
    private String code;

    @NotNull(message = "Input stock is required")
    @Min(value = 1, message = "Input stock must be greater than 0")
    private Integer input_stock;

    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.00", message = "Value must be greater than 0")
    private Float value;

    @Future(message = "Available date must be in the future")
    @NotNull(message = "Available date is required")
    private LocalDateTime available_date;

    @Future(message = "Expired date must be in the future")
    @NotNull(message = "Expired date is required")
    private LocalDateTime expired_date;
}
