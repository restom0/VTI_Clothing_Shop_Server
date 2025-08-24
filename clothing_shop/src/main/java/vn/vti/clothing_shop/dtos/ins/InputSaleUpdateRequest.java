package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record InputSaleUpdateRequest(

        @NotNull(message = "Sale price is required")
        @Positive
        Float salePercentage,

        @NotNull(message = "Discount is required")
        @Positive
        @DecimalMax(value = "100.00", message = "Discount must be less than or equal to 100")
        Float discount,

        LocalDate availableDate,

        @Future(message = "End date must be in the future")
        LocalDate endDate,

        @NotNull
        @Positive
        Long version
) implements DateRange {

}
