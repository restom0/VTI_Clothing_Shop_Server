package vn.vti.clothing_shop.dtos.ins;

import java.time.LocalDate;

public interface DateRange {
	/** Handles available date. */
	LocalDate availableDate();

	/** Handles end date. */
	LocalDate endDate();
}
