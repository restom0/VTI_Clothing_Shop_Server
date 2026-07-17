package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.OrderCheckoutRequest;
import vn.vti.clothing_shop.dtos.ins.OrderConfirmRequest;
import vn.vti.clothing_shop.dtos.ins.OrderCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateRequest;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface OrderService {
	/** Gets all orders. */
	List<Order> getAllOrders();

	/** Gets all orders by user id. */
	List<Order> getAllOrdersByUserId(Long userId);

	/** Gets order by id and user id. */
	Order getOrderByIdAndUserId(Long id, Long userId) throws WrapperException;

	/** Adds order. */
	Order addOrder(OrderCreateRequest orderCreateRequest, Long userId) throws WrapperException;

	/** Updates order. */
	void updateOrder(Long id, OrderUpdateRequest orderUpdateRequest) throws WrapperException;

	/** Deletes order. */
	void deleteOrder(Long id) throws WrapperException;

	/** Gets order by id and user id. */
	Order getOrderByIdAndUserId(OrderCheckoutRequest orderCheckoutRequest, Long userId) throws WrapperException;

	/** Confirms order. */
	Boolean confirmOrder(OrderConfirmRequest orderConfirmRequest, Long userId) throws WrapperException;
}
