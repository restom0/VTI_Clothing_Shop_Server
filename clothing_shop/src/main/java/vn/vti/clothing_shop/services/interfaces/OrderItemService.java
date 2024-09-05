package vn.vti.clothing_shop.services.interfaces;

import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.OrderItemCreateDTO;
import vn.vti.clothing_shop.dtos.ins.OrderItemUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.OrderItemDTO;

import java.util.List;

@Component
public interface OrderItemService {
    List<OrderItemDTO> getAllOrderItem();
    List<OrderItemDTO> getAllOrderItemsByOrderId(Long orderId);
    OrderItemDTO getOrderItemByIdAndOrderId(Long id,Long orderId);
    Boolean addOrderItem(OrderItemCreateDTO orderItemCreateDTO);
    Boolean updateOrderItem(OrderItemUpdateDTO orderItemUpdateDTO);
    Boolean deleteOrderItem(Long id,Long orderId);
}
