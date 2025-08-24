package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.validators.PhoneNumber;

import java.time.LocalDate;

public record UserUpdateRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email
        String email,

        @NotNull(message = "Phone number is required")
        @PhoneNumber
        String phoneNumber,

        String address,

        LocalDate birthday,

        String avatarUrl,

        String publicIdAvatarUrl,

        @NotNull(message = "Gender is required")
        UserGender gender
) {

}
