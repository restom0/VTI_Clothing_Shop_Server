package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import vn.vti.clothing_shop.validators.EndDateAfterStartDate;

import java.time.LocalDate;

/** Creates VoucherUpdateRequest instance. */
@EndDateAfterStartDate
public record VoucherUpdateRequest(
		@NotNull(message = "{messages.validation.required}")
		String code,

		@NotNull(message = "{messages.validation.required}")
		@Positive
		Integer inputStock,

		@NotNull(message = "{messages.validation.required}")
		@Positive
		Float value,

		@NotNull(message = "{messages.validation.required}")
		LocalDate availableDate,

		@Future(message = "{messages.validation.future}")
		LocalDate endDate,

		@NotNull
		Long version
) {

}
