package vn.vti.clothing_shop.services.interfaces;

import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.OrderItemCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderItemUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.OrderItemDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

@Component
public interface OrderItemService {
    List<OrderItemDTO> getAllOrderItems();

    List<OrderItemDTO> getAllOrderItemsByOrderId(Long orderId);

    OrderItemDTO findOrderItemByIdAndOrderId(Long id, Long orderId) throws WrapperException;

    void addOrderItem(OrderItemCreateRequest orderItemCreateRequest) throws WrapperException;

    void updateOrderItem(OrderItemUpdateRequest orderItemUpdateRequest, Long userId, Long orderId, Long orderItemId) throws WrapperException;

    void deleteOrderItem(Long id, Long orderId) throws WrapperException;
}
