package vn.vti.clothing_shop.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.vti.clothing_shop.dtos.ins.DateRange;

import java.time.LocalDate;

public class EndDateAfterStartDateValidator implements ConstraintValidator<EndDateAfterStartDate, DateRange> {

    @Override
    public boolean isValid(DateRange dateRange, ConstraintValidatorContext context) {
        LocalDate start = dateRange.availableDate();
        LocalDate end = dateRange.endDate();
        if (start == null || end == null) return true;
        return !end.isBefore(start);
    }

}

