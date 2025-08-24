package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import vn.vti.clothing_shop.constants.InputSaleFilter;

import java.time.LocalDate;

public record InputSaleCreateRequest(
        @NotNull(message = "Filter is required")
        InputSaleFilter filter,

        @NotNull(message = "Filter id is required")
        @Positive
        Long filterId,

        @NotNull(message = "Sale price is required")
        @Positive
        Float salePercentage,

        @NotNull(message = "Discount is required")
        @PositiveOrZero
        @DecimalMax(value = "100.00", message = "Discount must be less than or equal to 100")
        Float discount,

        @NotNull(message = "Available date is required")
        LocalDate availableDate,

        @Future(message = "End date must be in the future")
        LocalDate endDate
) implements DateRange {

}
