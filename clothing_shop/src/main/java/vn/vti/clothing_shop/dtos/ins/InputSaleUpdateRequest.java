package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record InputSaleUpdateRequest(

        @NotNull(message = "{messages.validation.required}")
        @Positive
        Float salePercentage,

        @NotNull(message = "{messages.validation.required}")
        @Positive
        @DecimalMax(value = "100.00", message = "{messages.validation.discount.max}")
        Float discount,

        LocalDate availableDate,

        @Future(message = "{messages.validation.future}")
        LocalDate endDate,

        @NotNull
        @Positive
        Long version
) implements DateRange {

}
