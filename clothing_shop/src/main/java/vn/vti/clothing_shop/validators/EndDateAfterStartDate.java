package vn.vti.clothing_shop.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EndDateAfterStartDateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EndDateAfterStartDate {
	/** Returns value. */
	String message() default "{messages.validation.endDateAfterStart}";

	/** Returns value. */
	Class<?>[] groups() default {};

	/** Returns value. */
	Class<? extends Payload>[] payload() default {};
}
