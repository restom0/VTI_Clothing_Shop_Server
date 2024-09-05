package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.OrderItemCreateDTO;
import vn.vti.clothing_shop.dtos.ins.OrderItemUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.OrderItemDTO;
import vn.vti.clothing_shop.entities.OnSaleProduct;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.entities.OrderItem;
import vn.vti.clothing_shop.requests.OrderItemCreateRequest;
import vn.vti.clothing_shop.requests.OrderItemUpdateRequest;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderItemMapper {
    private final ModelMapper modelMapper;
    private final OnSaleProductMapper onSaleProductMapper;

    @Autowired
    public OrderItemMapper(ModelMapper modelMapper, OnSaleProductMapper onSaleProductMapper) {
        this.modelMapper = modelMapper;
        this.onSaleProductMapper = onSaleProductMapper;
    }

    public OrderItemDTO EntityToDTO(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = modelMapper.map(orderItem, OrderItemDTO.class);
        orderItemDTO.setProduct_id(onSaleProductMapper.EntityToDTO(orderItem.getProduct_id()));
        return orderItemDTO;
    }

    public List<OrderItemDTO> ListEntityToListDTO(List<OrderItem> orderItems) {
        if (orderItems.isEmpty())   return new ArrayList<>();
        return orderItems.stream()
                .map(this::EntityToDTO)
                .toList();
    }

    public OrderItemCreateDTO CreateRequestToCreateDTO(OrderItemCreateRequest orderItemCreateRequest) {
        return modelMapper.map(orderItemCreateRequest, OrderItemCreateDTO.class);
    }

    public OrderItem CreateDTOToEntity(OrderItemCreateDTO orderItemCreateDTO, Order order, OnSaleProduct onSaleProduct) {
        OrderItem orderItem = modelMapper.map(orderItemCreateDTO, OrderItem.class);
        orderItem.setOrder_id(order);
        orderItem.setProduct_id(onSaleProduct);
        return orderItem;
    }

    public OrderItemUpdateDTO UpdateRequestToUpdateDTO(OrderItemUpdateRequest orderItemUpdateRequest, Long user_id) {
        OrderItemUpdateDTO orderItemUpdateDTO = modelMapper.map(orderItemUpdateRequest, OrderItemUpdateDTO.class);
        orderItemUpdateDTO.setUser_id(user_id);
        return orderItemUpdateDTO;
    }

    public OrderItem UpdateDTOToEntity(OrderItemUpdateDTO orderItemUpdateDTO, OrderItem orderItem) {
        orderItem.setQuantity(orderItemUpdateDTO.getQuantity());
        return orderItem;
    }
}
