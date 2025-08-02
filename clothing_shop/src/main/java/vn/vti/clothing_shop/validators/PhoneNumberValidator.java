package vn.vti.clothing_shop.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static vn.vti.clothing_shop.constants.RegularExpression.PHONE_NUMBER;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && PHONE_NUMBER.matcher(value).matches();
    }
}
