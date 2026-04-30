package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.dtos.outs.PaymentCheckoutResponse;
import vn.vti.clothing_shop.exceptions.WrapperException;

public interface PaymentService {
    PaymentCheckoutResponse createCheckout(OrderDTO orderDTO) throws WrapperException;
}
