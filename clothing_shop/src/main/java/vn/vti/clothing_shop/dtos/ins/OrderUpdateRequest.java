package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.validators.PhoneNumber;

public record OrderUpdateRequest(
        @NotBlank(message = "Address is required")

        String address,

        @NotBlank(message = "Phone_number is required")
        @PhoneNumber
        String phoneNumber,

        @NotBlank(message = "receiver_name is required")
        @Size(max = 255)
        String receiverName,

        @NotNull(message = "isPresent is required")
        Boolean isPresent,

        @NotBlank(message = "payment_method is required")
        PaymentMethod paymentMethod,

        Long voucherId
) {

}
