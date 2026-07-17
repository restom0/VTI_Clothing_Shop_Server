package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.validators.PhoneNumber;

import java.time.LocalDate;

/** Creates UserUpdateRequest instance. */
public record UserUpdateRequest(
		@NotBlank(message = "{messages.validation.required}")
		String name,

		@NotBlank(message = "{messages.validation.required}")
		@Email
		String email,

		@NotNull(message = "{messages.validation.required}")
		@PhoneNumber
		String phoneNumber,

		String address,

		LocalDate birthday,

		String avatarUrl,

		String publicIdAvatarUrl,

		@NotNull(message = "{messages.validation.required}")
		UserGender gender
) {

}
