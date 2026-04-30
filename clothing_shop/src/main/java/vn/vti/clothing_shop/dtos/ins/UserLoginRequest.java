package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotNull;

public record UserLoginRequest(
        @NotNull(message = "{messages.validation.required}")
        String usernameOrEmailOrPhoneNumber,

        @NotNull(message = "{messages.validation.required}")
        String password
) {

}
