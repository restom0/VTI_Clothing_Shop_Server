package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.validators.PhoneNumber;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @PhoneNumber
    private String phoneNumber;

    private String address;

    private LocalDate birthday;

    private String avatarUrl;
    private String publicIdAvatarUrl;

    @NotNull(message = "Gender is required")
    private UserGender gender;
}
