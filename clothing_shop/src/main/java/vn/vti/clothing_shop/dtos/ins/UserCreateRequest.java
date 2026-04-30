package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.validators.PhoneNumber;

import java.time.LocalDate;

public record UserCreateRequest(
        @NotBlank(message = "{messages.validation.required}")
        String name,

        @NotBlank(message = "{messages.validation.required}")
        String username,

        @NotBlank(message = "{messages.validation.required}")
        String password,

        @Email(message = "{messages.validation.email.invalid}")
        String email,

        @NotBlank(message = "{messages.validation.required}") @PhoneNumber
        String phoneNumber,

        String address,

        @Past
        LocalDate birthday,

        String avatarUrl,

        String publicIdAvatarUrl,

        @NotNull(message = "{messages.validation.required}")
        UserGender gender
) {

}
