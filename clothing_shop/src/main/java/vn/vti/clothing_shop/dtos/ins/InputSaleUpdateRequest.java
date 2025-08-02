package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record InputSaleUpdateRequest(

        @NotNull(message = "Sale price is required")
        @DecimalMin(value = "0.00", message = "Sale price must be greater than 0")
        Float salePercentage,

        @NotNull(message = "Discount is required")
        @DecimalMin(value = "0.00", message = "Discount must be greater than 0")
        @DecimalMax(value = "100.00", message = "Discount must be less than or equal to 100")
        Float discount,

        LocalDate availableDate,

        @Future(message = "End date must be in the future")
        LocalDate endDate
) {

}
