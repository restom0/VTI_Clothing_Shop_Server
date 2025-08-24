package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.OrderItemUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.OrderItemDTO;
import vn.vti.clothing_shop.entities.OnSaleProduct;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.entities.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {OnSaleProductMapper.class})
public interface OrderItemMapper {
    OrderItemDTO entityToDTO(OrderItem orderItem);

    List<OrderItemDTO> listEntityToListDTO(List<OrderItem> orderItems);

    OrderItem createRequestToEntity(OnSaleProduct product, Order order);

    OrderItem updateRequestToEntity(OrderItemUpdateRequest orderItemUpdateRequest, OnSaleProduct onSaleProduct, @MappingTarget OrderItem orderItem);

}