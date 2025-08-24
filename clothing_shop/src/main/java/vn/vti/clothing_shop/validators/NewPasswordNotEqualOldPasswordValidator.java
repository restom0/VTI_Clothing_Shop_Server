package vn.vti.clothing_shop.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.vti.clothing_shop.dtos.ins.UserUpdatePasswordRequest;

public class NewPasswordNotEqualOldPasswordValidator implements ConstraintValidator<EndDateAfterStartDate, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof UserUpdatePasswordRequest dto) {
            String newPassword = dto.password();
            String oldPassword = dto.oldPassword();
            return !newPassword.equals(oldPassword);
        }
        return false;
    }

}

