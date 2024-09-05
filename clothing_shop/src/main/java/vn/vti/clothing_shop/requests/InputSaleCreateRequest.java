package vn.vti.clothing_shop.requests;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.InputSaleFilter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InputSaleCreateRequest {
    @NotNull(message = "Filter is required")
    private InputSaleFilter filter;

    @NotNull(message = "Filter id is required")
    @Min(value = 1, message = "Filter id must be greater than 0")
    @Max(value = Long.MAX_VALUE, message = "Filter id must be less than or equal to " + Long.MAX_VALUE)
    private Long filter_id;

    @NotNull(message = "Sale price is required")
    @DecimalMin(value = "0.00", message = "Sale price must be greater than 0")
    private Float salePercentage;

    @NotNull(message = "Discount is required")
    @DecimalMin(value = "0.00", message = "Discount must be greater than 0")
    @DecimalMax(value = "100.00", message = "Discount must be less than or equal to 100")
    private Float discount;

    //@Future(message = "Available date must be in the future")
    private LocalDateTime available_date;

    private LocalDateTime end_date;
}
