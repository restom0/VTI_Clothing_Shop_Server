package vn.vti.clothing_shop.dtos.ins;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import vn.vti.clothing_shop.validators.PhoneNumber;

public record OrderCreateRequest(
        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address must be less than 255 characters")
        String address,

        @PhoneNumber
        String phoneNumber,

        @NotBlank(message = "Receiver Name is required")
        String receiverName
) {

}
