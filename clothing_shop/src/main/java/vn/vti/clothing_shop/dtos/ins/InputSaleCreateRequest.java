package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import vn.vti.clothing_shop.constants.InputSaleFilter;

import java.time.LocalDate;

public record InputSaleCreateRequest(
        @NotNull(message = "Filter is required")
        InputSaleFilter filter,

        @NotNull(message = "Filter id is required")
        @Min(value = 1, message = "Filter id must be greater than 0")
        Long filterId,

        @NotNull(message = "Sale price is required")
        @DecimalMin(value = "0.00", message = "Sale price must be greater than 0")
        Float salePercentage,

        @NotNull(message = "Discount is required")
        @DecimalMin(value = "0.00", message = "Discount must be greater than 0")
        @DecimalMax(value = "100.00", message = "Discount must be less than or equal to 100")
        Float discount,

        @NotNull(message = "Available date is required")
        LocalDate availableDate,

        @Future(message = "End date must be in the future")
        LocalDate endDate
) {

}
