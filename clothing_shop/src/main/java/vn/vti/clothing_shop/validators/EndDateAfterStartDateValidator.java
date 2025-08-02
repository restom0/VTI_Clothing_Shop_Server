package vn.vti.clothing_shop.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateRequest;

import java.time.LocalDate;

import static vn.vti.clothing_shop.constants.RegularExpression.PHONE_NUMBER;

public class EndDateAfterStartDateValidator implements ConstraintValidator<EndDateAfterStartDate, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof InputSaleCreateRequest dto) {
            LocalDate start = dto.availableDate();
            LocalDate end = dto.endDate();
            if (start == null || end == null) return true;
            return end.isAfter(start);
        } else if (obj instanceof InputSaleUpdateRequest dto) {
            LocalDate start = dto.availableDate();
            LocalDate end = dto.endDate();
            if (start == null || end == null) return true;
            return end.isAfter(start);
        }
        return false;
    }

}

