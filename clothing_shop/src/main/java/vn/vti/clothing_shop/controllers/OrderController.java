package vn.vti.clothing_shop.controllers;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.vti.clothing_shop.dtos.ins.OrderCheckoutRequest;
import vn.vti.clothing_shop.dtos.ins.OrderConfirmRequest;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.OrderMapper;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.OrderItemService;
import vn.vti.clothing_shop.services.interfaces.OrderService;
import vn.vti.clothing_shop.services.interfaces.PaymentService;

@AllArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {
	private final OrderService orderService;
	private final OrderItemService orderItemService;
	private final PaymentService paymentService;
	private final OrderMapper orderMapper;

	/** Gets all orders. */
	@Operation(summary = "Gets all orders", description = "Gets all orders API.")
	@GetMapping
	public ResponseEntity<BaseMessageResponse> getAllOrders() {
		return ResponseHandler.successBuilder(HttpStatus.OK, orderService.getAllOrders().stream()
		                                                                 .map(orderMapper::entityToDTO)
		                                                                 .toList());
	}

	/** Gets all orders by user id. */
	@Operation(summary = "Gets all orders by user id", description = "Gets all orders by user id API.")
	@GetMapping("/user")
	public ResponseEntity<BaseMessageResponse> getAllOrdersByUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		Long userId = user.getId();
		return ResponseHandler.successBuilder(HttpStatus.OK, orderService.getAllOrdersByUserId(userId).stream()
		                                                                 .map(orderMapper::entityToDTO)
		                                                                 .toList());
	}

	/** Adds order. */
	@Operation(summary = "Adds order", description = "Adds order API.")
	@GetMapping("/cart")
	public ResponseEntity<BaseMessageResponse> addOrder() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) authentication.getPrincipal();
			Long userId = user.getId();
			OrderDTO orderDTO = orderMapper.entityToDTO(orderService.addOrder(null, userId));
			return ResponseHandler.successBuilder(HttpStatus.OK, orderDTO);
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Updates order. */
	@Operation(summary = "Updates order", description = "Updates order API.")
	@PutMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> updateOrder(
			@RequestBody @NotNull(message = "{messages.validation.required}") OrderUpdateRequest orderUpdateRequest,
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id) {
		try {
			orderService.updateOrder(id, orderUpdateRequest);
			return ResponseHandler.successBuilder(HttpStatus.OK, "messages.orders.updated");
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Deletes order. */
	@Operation(summary = "Deletes order", description = "Deletes order API.")
	@DeleteMapping("/{orderId}/{id}")
	public ResponseEntity<BaseMessageResponse> deleteOrder(
			@PathVariable @NotNull(message = "{messages.validation.required}") Long orderId,
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id) {
		try {
			orderItemService.deleteOrderItem(id, orderId);
			orderService.deleteOrder(orderId);
			return ResponseHandler.successBuilder(HttpStatus.OK, "messages.orders.deleted");
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Creates payment link. */
	@Operation(summary = "Creates payment link", description = "Creates payment link API.")
	@PostMapping(path = "/checkout")
	public ResponseEntity<BaseMessageResponse> createPaymentLink(
			@RequestBody @NotNull(message = "{messages.validation.required}") OrderCheckoutRequest orderCheckoutRequest) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) authentication.getPrincipal();
			Long userId = user.getId();
			OrderDTO orderDTO = orderMapper.entityToDTO(orderService.getOrderByIdAndUserId(orderCheckoutRequest, userId));
			return ResponseHandler.successBuilder(HttpStatus.OK, "messages.orders.paymentLinkCreated",
			                                      paymentService.createCheckout(orderDTO));
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Handles success. */
	@Operation(summary = "Handles success", description = "Handles success API.")
	@PutMapping("/success")
	public ResponseEntity<BaseMessageResponse> success(
			@RequestBody @NotNull(message = "{messages.validation.required}") OrderConfirmRequest orderConfirmRequest) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) authentication.getPrincipal();
			Long userId = user.getId();
			return ResponseHandler.successBuilder(HttpStatus.OK, orderService.confirmOrder(orderConfirmRequest, userId));
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Handles cancel. */
	@Operation(summary = "Handles cancel", description = "Handles cancel API.")
	@PutMapping("/cancel")
	public ResponseEntity<BaseMessageResponse> cancel(
			@RequestBody @NotNull(message = "{messages.validation.required}") OrderConfirmRequest orderConfirmRequest) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) authentication.getPrincipal();
			Long userId = user.getId();
			return ResponseHandler.successBuilder(HttpStatus.OK, orderService.confirmOrder(orderConfirmRequest, userId));
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}
}
