package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.OrderItemCreateRequest;
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

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "product", source = "product")
	@Mapping(target = "order", source = "order")
	@Mapping(target = "quantity", source = "orderItemCreateRequest.quantity")
    OrderItem createRequestToEntity(OrderItemCreateRequest orderItemCreateRequest, OnSaleProduct product, Order order);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", source = "orderItemUpdateRequest.version")
	@Mapping(target = "product", source = "onSaleProduct")
	@Mapping(target = "order", ignore = true)
    OrderItem updateRequestToEntity(OrderItemUpdateRequest orderItemUpdateRequest, OnSaleProduct onSaleProduct, @MappingTarget OrderItem orderItem);

}
