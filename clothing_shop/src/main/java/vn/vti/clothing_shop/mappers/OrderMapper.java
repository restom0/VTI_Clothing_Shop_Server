package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.OrderCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.entities.Voucher;

@Mapper(componentModel = "spring",
        uses = {OrderItemMapper.class})
public interface OrderMapper {
    OrderDTO entityToDTO(Order order);

	@Mapping(target = "address", source = "orderCreateRequest.address")
	@Mapping(target = "phoneNumber", source = "orderCreateRequest.phoneNumber")
    Order createRequestToEntity(OrderCreateRequest orderCreateRequest, User user);

    Order updateRequestToEntity(OrderUpdateRequest orderUpdateRequest, Voucher voucher, @MappingTarget Order order);
}
