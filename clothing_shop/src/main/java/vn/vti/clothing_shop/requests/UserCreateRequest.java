package vn.vti.clothing_shop.requests;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.constants.UserRole;

import java.time.LocalDate;

import static vn.vti.clothing_shop.constants.RegularExpression.EMAIL;
import static vn.vti.clothing_shop.constants.RegularExpression.PHONE_NUMBER;

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

    @Pattern(regexp = EMAIL,message = "Invalid email")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = PHONE_NUMBER,message = "Invalid phone number")
    private String phone_number;

    private String address;

    private LocalDate birthday;

    private String avatar_url;
    private String public_id_avatar_url;

    @NotNull(message = "Gender is required")
    private UserGender gender;
}
