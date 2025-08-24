package vn.vti.clothing_shop.dtos.ins;

import java.time.LocalDate;

public interface DateRange {
    LocalDate availableDate();

    LocalDate endDate();
}
