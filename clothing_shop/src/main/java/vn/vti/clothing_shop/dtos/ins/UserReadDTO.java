package vn.vti.clothing_shop.dtos.ins;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReadDTO {
    private String username;
    private String email;
    private String phone_number;
    private String password;
    @Override
    public String toString() {
        return "UserReadDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
