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
        uses = { OrderItemMapper.class })
public interface OrderMapper {
	/** Maps to DTO. */
	OrderDTO entityToDTO(Order order);

	/** Creates request to entity. */
	@Mapping(target = "address", source = "orderCreateRequest.address")
	@Mapping(target = "phoneNumber", source = "orderCreateRequest.phoneNumber")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", source = "user")
	@Mapping(target = "voucher", ignore = true)
	@Mapping(target = "totalPrice", constant = "0L")
	@Mapping(target = "orderCode", expression = "java(System.currentTimeMillis())")
	@Mapping(target = "paymentStatus", expression = "java(vn.vti.clothing_shop.constants.PaymentStatus.NOT_CONFIRMED)")
	@Mapping(target = "paymentMethod", expression = "java(vn.vti.clothing_shop.constants.PaymentMethod.COD)")
	@Mapping(target = "isPresent", constant = "false")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	Order createRequestToEntity(OrderCreateRequest orderCreateRequest, User user);

	/** Updates request to entity. */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "voucher", source = "voucher")
	@Mapping(target = "totalPrice", ignore = true)
	@Mapping(target = "orderCode", ignore = true)
	@Mapping(target = "paymentStatus", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	Order updateRequestToEntity(OrderUpdateRequest orderUpdateRequest, Voucher voucher, @MappingTarget Order order);
}
