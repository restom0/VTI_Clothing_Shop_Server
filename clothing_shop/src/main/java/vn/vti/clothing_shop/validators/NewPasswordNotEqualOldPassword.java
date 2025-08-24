package vn.vti.clothing_shop.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NewPasswordNotEqualOldPasswordValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NewPasswordNotEqualOldPassword {
    String message() default "New password must not be equal to old password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
