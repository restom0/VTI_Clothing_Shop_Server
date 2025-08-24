package vn.vti.clothing_shop.dtos.outs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.constants.PaymentStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private String address;
    private String phoneNumber;
    private String receiverName;
    private Boolean isPresent;
    private Long totalPrice;
    private Long orderCode;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private VoucherDTO voucher;
    private List<OrderItemDTO> orderItems;
}
