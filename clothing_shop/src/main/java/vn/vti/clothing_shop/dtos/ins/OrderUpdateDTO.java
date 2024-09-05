package vn.vti.clothing_shop.dtos.ins;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.PaymentMethod;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateDTO {
    private Long id;
    private String address;
    private String phone_number;
    private String receiver_name;
    private Boolean isPresent;
    private PaymentMethod payment_method;
    private Long voucherId;
}
