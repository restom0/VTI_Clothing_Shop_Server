package vn.vti.clothing_shop.services.interfaces;

import org.springframework.stereotype.Component;

import vn.vti.clothing_shop.dtos.ins.OrderItemCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderItemUpdateRequest;
import vn.vti.clothing_shop.entities.OrderItem;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

@Component
public interface OrderItemService {
	/** Gets all order items. */
	List<OrderItem> getAllOrderItems();

	/** Gets all order items by order id. */
	List<OrderItem> getAllOrderItemsByOrderId(Long orderId);

	/** Finds order item by id and order id. */
	OrderItem findOrderItemByIdAndOrderId(Long id, Long orderId) throws WrapperException;

	/** Adds order item. */
	void addOrderItem(OrderItemCreateRequest orderItemCreateRequest) throws WrapperException;

	/** Updates order item. */
	void updateOrderItem(OrderItemUpdateRequest orderItemUpdateRequest, Long userId, Long orderId, Long orderItemId)
			throws WrapperException;

	/** Deletes order item. */
	void deleteOrderItem(Long id, Long orderId) throws WrapperException;
}
