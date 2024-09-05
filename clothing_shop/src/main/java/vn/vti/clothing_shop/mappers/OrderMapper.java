package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.constants.PaymentStatus;
import vn.vti.clothing_shop.dtos.ins.OrderCheckoutDTO;
import vn.vti.clothing_shop.dtos.ins.OrderConfirmDTO;
import vn.vti.clothing_shop.dtos.ins.OrderCreateDTO;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.entities.OrderItem;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.requests.OrderCheckoutRequest;
import vn.vti.clothing_shop.requests.OrderConfirmRequest;
import vn.vti.clothing_shop.requests.OrderCreateRequest;
import vn.vti.clothing_shop.requests.OrderUpdateRequest;

import java.util.Date;
import java.util.List;

import static vn.vti.clothing_shop.constants.PaymentStatus.ON_HOLD;

@Component
public class OrderMapper {
    private final ModelMapper modelMapper;
    private final OrderItemMapper orderItemMapper;

    @Autowired
    public OrderMapper(ModelMapper modelMapper, OrderItemMapper orderItemMapper) {
        this.modelMapper = modelMapper;
        this.orderItemMapper = orderItemMapper;
    }

    public OrderDTO EntityToDTO(Order order, List<OrderItem> orderItems) {
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        orderDTO.setOrderItems(orderItemMapper.ListEntityToListDTO(orderItems));
        return orderDTO;
    }

    public List<OrderDTO> ListEntityToListDTO(List<Order> orders, List<List<OrderItem>> orderItems) {
        return orders.stream()
                .map(order -> EntityToDTO(order, orderItems.get(orders.indexOf(order))))
                .toList();
    }

    public OrderCreateDTO CreateRequestToCreateDTO(OrderCreateRequest orderCreateRequest, Long user_id) {
        OrderCreateDTO orderCreateDTO = modelMapper.map(orderCreateRequest, OrderCreateDTO.class);
        orderCreateDTO.setUser_id(user_id);
        return orderCreateDTO;
    }

    public Order CreateDTOToEntity(OrderCreateDTO orderCreateDTO, User user) {
        Order order = modelMapper.map(orderCreateDTO, Order.class);
        String currentTimeString = String.valueOf(new Date().getTime());
        order.setAddress(user.getAddress());
        order.setPhone_number(user.getPhone_number());
        order.setReceiver_name(user.getName());
        order.setIsPresent(false);
        order.setPayment_method(PaymentMethod.COD);
        order.setPayment_status(PaymentStatus.NOT_CONFIRMED);
        order.setTotal_price(0L);
        order.setOrder_code(Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6)));
        order.setUser_id(user);
        return order;
    }

    public OrderUpdateDTO UpdateRequestToUpdateDTO(OrderUpdateRequest orderUpdateRequest,Long id) {
        OrderUpdateDTO orderUpdateDTO = modelMapper.map(orderUpdateRequest, OrderUpdateDTO.class);
        orderUpdateDTO.setId(id);
        return orderUpdateDTO;
    }

    public Order UpdateDTOToEntity(OrderUpdateDTO orderUpdateDTO, Order order, Voucher voucher) {
        order.setAddress(orderUpdateDTO.getAddress());
        order.setPhone_number(orderUpdateDTO.getPhone_number());
        order.setReceiver_name(orderUpdateDTO.getReceiver_name());
        order.setPayment_status(ON_HOLD);
        order.setIsPresent(orderUpdateDTO.getIsPresent());
        order.setPayment_method(orderUpdateDTO.getPayment_method());
        order.setVoucherId(voucher);
        return order;
    }

    public OrderCheckoutDTO CheckoutRequestToCheckoutDTO(OrderCheckoutRequest orderCheckoutRequest, Long user_id) {
        OrderCheckoutDTO orderCheckoutDTO = modelMapper.map(orderCheckoutRequest, OrderCheckoutDTO.class);
        orderCheckoutDTO.setUser_id(user_id);
        return orderCheckoutDTO;
    }
    public OrderConfirmDTO ConfirmRequestToConfirmDTO(OrderConfirmRequest orderConfirmRequest, Long user_id,Boolean status) {
        OrderConfirmDTO orderConfirmDTO = modelMapper.map(orderConfirmRequest, OrderConfirmDTO.class);
        orderConfirmDTO.setUser_id(user_id);
        orderConfirmDTO.setStatus(status);
        return orderConfirmDTO;
    }
}
