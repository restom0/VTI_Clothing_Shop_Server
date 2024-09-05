package vn.vti.clothing_shop.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static vn.vti.clothing_shop.constants.RegularExpression.BOOLEAN;
import static vn.vti.clothing_shop.constants.RegularExpression.PHONE_NUMBER;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @NotBlank(message = "address is required")
    private String address;

    @Pattern(regexp= PHONE_NUMBER,message = "Invalid phone number")
    private String phone_number;

    @NotBlank(message = "receiver_name is required")
    private String receiver_name;

}
