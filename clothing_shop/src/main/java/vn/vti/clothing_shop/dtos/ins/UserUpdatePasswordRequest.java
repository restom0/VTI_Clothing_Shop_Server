package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vn.vti.clothing_shop.validators.NewPasswordNotEqualOldPassword;

@NewPasswordNotEqualOldPassword
public record UserUpdatePasswordRequest(
        @NotBlank(message = "Old password is required")
        String oldPassword,
        @NotBlank(message = "New password is required")
        String password,
        @NotNull
        Long version
) {

}
