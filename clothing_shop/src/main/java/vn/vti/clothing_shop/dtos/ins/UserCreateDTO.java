package vn.vti.clothing_shop.dtos.ins;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.UserGender;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {
    private String name;
    private String username;
    private String password;
    private String email;
    private String phone_number;
    private String address;
    private LocalDate birthday;
    private String avatar_url;
    private String public_id_avatar_url;
    private UserGender gender;
}
