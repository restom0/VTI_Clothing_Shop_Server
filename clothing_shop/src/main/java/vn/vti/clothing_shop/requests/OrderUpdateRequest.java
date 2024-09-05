package vn.vti.clothing_shop.requests;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.constants.PaymentStatus;

import static vn.vti.clothing_shop.constants.RegularExpression.BOOLEAN;
import static vn.vti.clothing_shop.constants.RegularExpression.PHONE_NUMBER;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateRequest {
    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "Phone_number is required")
    @Pattern(regexp= PHONE_NUMBER,message = "Invalid phone number")
    private String phone_number;

    @NotBlank(message = "receiver_name is required")
    private String receiver_name;

    @NotNull(message = "isPresent is required")
    private Boolean isPresent;

    @NotBlank(message = "payment_method is required")
    private PaymentMethod payment_method;

    private Long voucherId;
}
