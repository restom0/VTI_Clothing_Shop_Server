package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.OrderCheckoutRequest;
import vn.vti.clothing_shop.dtos.ins.OrderConfirmRequest;
import vn.vti.clothing_shop.dtos.ins.OrderCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateRequest;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface OrderService {
	List<Order> getAllOrders();

	List<Order> getAllOrdersByUserId(Long userId);

	Order getOrderByIdAndUserId(Long id, Long userId) throws WrapperException;

	Order addOrder(OrderCreateRequest orderCreateRequest, Long userId) throws WrapperException;

	void updateOrder(Long id, OrderUpdateRequest orderUpdateRequest) throws WrapperException;

	void deleteOrder(Long id) throws WrapperException;

	Order getOrderByIdAndUserId(OrderCheckoutRequest orderCheckoutRequest, Long userId) throws WrapperException;

	Boolean confirmOrder(OrderConfirmRequest orderConfirmRequest, Long userId) throws WrapperException;
}
