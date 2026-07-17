package vn.vti.clothing_shop.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.vti.clothing_shop.dtos.ins.UserUpdatePasswordRequest;

public class NewPasswordNotEqualOldPasswordValidator
		implements ConstraintValidator<NewPasswordNotEqualOldPassword, UserUpdatePasswordRequest> {

	/** Checks whether valid. */
	@Override
	public boolean isValid(UserUpdatePasswordRequest dto, ConstraintValidatorContext context) {
		if (dto == null || dto.password() == null || dto.oldPassword() == null) {
			return true;
		}
		return !dto.password().equals(dto.oldPassword());
	}

}

