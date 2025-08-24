package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import vn.vti.clothing_shop.validators.EndDateAfterStartDate;

import java.time.LocalDate;

@EndDateAfterStartDate
public record VoucherCreateRequest(
        @NotNull(message = "Code is required")
        String code,

        @NotNull(message = "Input stock is required")
        @Positive
        Integer inputStock,

        @NotNull(message = "Value is required")
        @Positive
        Float value,

        @NotNull(message = "Available date is required")
        LocalDate availableDate,

        @Future(message = "Expired date must be in the future")
        LocalDate endDate
) {

}
