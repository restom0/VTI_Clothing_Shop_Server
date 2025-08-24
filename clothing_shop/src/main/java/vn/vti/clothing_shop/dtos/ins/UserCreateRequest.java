package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.validators.PhoneNumber;

import java.time.LocalDate;

public record UserCreateRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password,

        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone number is required") @PhoneNumber
        String phoneNumber,

        String address,

        @Past
        LocalDate birthday,

        String avatarUrl,

        String publicIdAvatarUrl,

        @NotNull(message = "Gender is required")
        UserGender gender
) {

}
