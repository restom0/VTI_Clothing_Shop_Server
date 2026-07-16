package vn.vti.clothing_shop.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static vn.vti.clothing_shop.constants.RegularExpression.PHONE_NUMBER;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
	private static final int MIN_PHONE_DIGITS = 9;
	private static final int MAX_PHONE_DIGITS = 15;

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || !PHONE_NUMBER.matcher(value).matches()) {
			return false;
		}
		long digitCount = value.chars().filter(Character::isDigit).count();
		return digitCount >= MIN_PHONE_DIGITS && digitCount <= MAX_PHONE_DIGITS;
	}
}
