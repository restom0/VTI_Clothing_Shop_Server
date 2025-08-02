package vn.vti.clothing_shop.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static vn.vti.clothing_shop.constants.RegularExpression.COLOR;

public class HexCodeValidator implements ConstraintValidator<HexCode, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && COLOR.matcher(value).matches();
    }
}
