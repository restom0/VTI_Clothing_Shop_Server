package vn.vti.clothing_shop.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
	/** Returns value. */
	String message() default "{messages.validation.phone.invalid}";

	/** Returns value. */
	Class<?>[] groups() default {};

	/** Returns value. */
	Class<? extends Payload>[] payload() default {};
}
