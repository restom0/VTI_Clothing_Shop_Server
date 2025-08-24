package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotNull;

public record UserLoginRequest(
        @NotNull(message = "Username, email or phone number is required")
        String usernameOrEmailOrPhoneNumber,

        @NotNull(message = "Password is required")
        String password
) {

}
