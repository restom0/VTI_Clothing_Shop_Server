package vn.vti.clothing_shop.requests;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReadRequest {
    private String username;
    private String email;
    private String phone_number;
    @NotNull(message = "Password is required")
    private String password;
}
