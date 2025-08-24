package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.OrderCheckoutRequest;
import vn.vti.clothing_shop.dtos.ins.OrderConfirmRequest;
import vn.vti.clothing_shop.dtos.ins.OrderCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getAllOrders();

    List<OrderDTO> getAllOrdersByUserId(Long userId);

    OrderDTO getOrderByIdAndUserId(Long id, Long userId) throws WrapperException;

    OrderDTO addOrder(OrderCreateRequest orderCreateRequest, Long userId) throws WrapperException;

    void updateOrder(Long id, OrderUpdateRequest orderUpdateRequest) throws WrapperException;

    void deleteOrder(Long id) throws WrapperException;

    OrderDTO getOrderByIdAndUserId(OrderCheckoutRequest orderCheckoutRequest, Long userId) throws WrapperException;

    Boolean confirmOrder(OrderConfirmRequest orderConfirmRequest, Long userId) throws WrapperException;
}
