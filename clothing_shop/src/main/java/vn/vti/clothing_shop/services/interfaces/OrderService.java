package vn.vti.clothing_shop.services.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.OrderCheckoutDTO;
import vn.vti.clothing_shop.dtos.ins.OrderConfirmDTO;
import vn.vti.clothing_shop.dtos.ins.OrderCreateDTO;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;

import java.util.List;

@Component
public interface OrderService {
    List<OrderDTO> getAllOrders();
    List<OrderDTO> getAllOrdersByUserId(Long userId);
    OrderDTO getOrderByIdAndUserId(Long id,Long userId);
    OrderDTO addOrder(OrderCreateDTO orderCreateDTO);
    Boolean updateOrder(OrderUpdateDTO orderUpdateDTO);
    Boolean deleteOrder(Long id);
    BrandDTO getMaxBrand();
    CategoryDTO getMaxCategory();
    OrderDTO getOrderByIdAndUserId(OrderCheckoutDTO orderCheckoutDTO);
    Boolean confirmOrder(OrderConfirmDTO orderConfirmDTO);
}
