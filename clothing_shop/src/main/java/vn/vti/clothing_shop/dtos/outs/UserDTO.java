package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.constants.UserRole;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String name;
    private LocalDate birthday;
    private String avatarUrl;
    private String publicIdAvatarUrl;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private String address;
    private UserGender gender;
}