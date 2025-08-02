package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePasswordRequest {
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "New password is required")
    private String newPassword;
}
