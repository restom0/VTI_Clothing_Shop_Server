package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.constants.Messages;
import vn.vti.clothing_shop.constants.PaymentStatus;
import vn.vti.clothing_shop.dtos.ins.OrderCheckoutRequest;
import vn.vti.clothing_shop.dtos.ins.OrderConfirmRequest;
import vn.vti.clothing_shop.dtos.ins.OrderCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateRequest;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.exceptions.ForbiddenException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.OrderMapper;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.repositories.VoucherRepository;
import vn.vti.clothing_shop.services.interfaces.OrderService;
import vn.vti.clothing_shop.utils.TimeUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final VoucherRepository voucherRepository;
	private final OrderMapper orderMapper;
	private final MongoReadModelQueryService readModelQueryService;
	private final PostgresToMongoReadModelSyncService readModelSyncService;

	/** Gets all orders. */
	@Cacheable(value = "orders", key = "'all'")
	@Override
	public List<Order> getAllOrders() {
		List<Order> mongoOrders = readModelQueryService.findAll(ReadModelType.ORDER, Order.class);
		if (mongoOrders != null && !mongoOrders.isEmpty()) {
			return mongoOrders;
		}
		return orderRepository.findByDeletedAtIsNullOrderByIdDesc();
	}

	/** Gets all orders by user id. */
	@Cacheable(value = "orders", key = "'user:' + #userId")
	@Override
	public List<Order> getAllOrdersByUserId(Long userId) {
		List<Order> mongoOrders = readModelQueryService.findByOwner(ReadModelType.ORDER, userId, Order.class);
		if (mongoOrders != null && !mongoOrders.isEmpty()) {
			return mongoOrders;
		}
		return orderRepository.findByDeletedAtIsNullAndUser_Id(userId);
	}

	/** Gets order by id and user id. */
	@Cacheable(value = "orders", key = "'id:' + #id + ':user:' + #userId")
	@Override
	public Order getOrderByIdAndUserId(Long id, Long userId) throws WrapperException {
		try {
			var mongoOrder = readModelQueryService.findByIdAndOwner(ReadModelType.ORDER, id, userId, Order.class);
			if (mongoOrder.isPresent()) {
				return mongoOrder.get();
			}
			return orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(id, userId)
			                      .orElseThrow(() -> new NotFoundException(Messages.MESSAGE_ORDER_NOTFOUND));
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	/** Adds order. */
	@CacheEvict(value = "orders", allEntries = true)
	@Transactional
	@Override
	public Order addOrder(OrderCreateRequest orderCreateRequest, Long userId) throws WrapperException {
		try {
			User user = userRepository.findById(userId).orElseThrow(() -> new ForbiddenException("messages.users.notfound"));
			Order order = orderRepository.findByDeletedAtIsNullAndUser_IdAndPaymentStatus(userId, PaymentStatus.NOT_CONFIRMED)
			                             .orElseGet(() -> orderRepository.save(
					                             orderMapper.createRequestToEntity(orderCreateRequest, user)));
			readModelSyncService.syncAfterCommit(ReadModelType.ORDER, order.getId());
			return order;
		} catch (ForbiddenException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Updates order. */
	@Caching(evict = {
			@CacheEvict(value = "orders", allEntries = true),
			@CacheEvict(value = "vouchers", allEntries = true)
	})
	@Transactional
	public void updateOrder(Long id, OrderUpdateRequest orderUpdateRequest) throws WrapperException {
		try {
			Order order = orderRepository.findById(id).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_ORDER_NOTFOUND));
			assertOpenOrder(order);
			Voucher voucher = voucherRepository.findById(orderUpdateRequest.voucherId()).orElseThrow(
					() -> new NotFoundException("messages.vouchers.notfound"));
			// Only spend stock when the voucher actually changes; re-sending the same update
			// must not decrement twice for one order.
			if (order.getVoucher() == null || !voucher.getId().equals(order.getVoucher().getId())) {
				adjustStock(voucher.getId(), NumberUtils.INTEGER_MINUS_ONE);
			}
			Order savedOrder = orderRepository.save(orderMapper.updateRequestToEntity(orderUpdateRequest, voucher, order));
			readModelSyncService.syncAfterCommit(ReadModelType.ORDER, savedOrder.getId());
		} catch (BadRequestException | NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Handles adjust stock. */
	private void adjustStock(Long id, Integer quantity) throws WrapperException {
		try {
			Voucher voucher = voucherRepository.findById(id).orElseThrow(
					() -> new NotFoundException("messages.vouchers.notfound"));
			Long now = TimeUtils.currentEpochMillis();
			if (voucher.getStock() + quantity < 0
					|| voucher.getEndDate() < now
					|| voucher.getAvailableDate() > now) {
				throw new BadRequestException("messages.vouchers.outOfStock");
			}
			voucher.setStock(voucher.getStock() + quantity);
			voucherRepository.save(voucher);
			readModelSyncService.syncAfterCommit(ReadModelType.VOUCHER, voucher.getId());
		} catch (NotFoundException | BadRequestException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Deletes order. */
	@Caching(evict = {
			@CacheEvict(value = "orders", allEntries = true),
			@CacheEvict(value = "vouchers", allEntries = true)
	})
	@Transactional
	@Override
	public void deleteOrder(Long id) throws WrapperException {
		try {
			Order order = orderRepository.findById(id).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_ORDER_NOTFOUND));
			assertOpenOrder(order);
			if (order.getVoucher() != null) {
				adjustStock(order.getVoucher().getId(), NumberUtils.INTEGER_ONE);
			}
			order.setPaymentStatus(PaymentStatus.CANCELLED);
			order.setDeletedAt(TimeUtils.currentEpochMillis());
			orderRepository.save(order);
			readModelSyncService.removeAfterCommit(ReadModelType.ORDER, id);
		} catch (BadRequestException | NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Gets order by id and user id. */
	@Cacheable(value = "orders", key = "'checkout:' + #orderCheckoutRequest.orderId() + ':user:' + #userId")
	@Override
	public Order getOrderByIdAndUserId(OrderCheckoutRequest orderCheckoutRequest, Long userId) throws WrapperException {
		try {
			var mongoOrder = readModelQueryService.findByIdAndOwner(ReadModelType.ORDER, orderCheckoutRequest.orderId(), userId,
			                                                        Order.class);
			if (mongoOrder.isPresent()) {
				return mongoOrder.get();
			}
			return orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(orderCheckoutRequest.orderId(), userId)
			                      .orElseThrow(() -> new NotFoundException(Messages.MESSAGE_ORDER_NOTFOUND));
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	/** Confirms order. */
	@CacheEvict(value = "orders", allEntries = true)
	@Transactional
	@Override
	public Boolean confirmOrder(OrderConfirmRequest orderConfirmRequest, Long userId) throws WrapperException {
		try {
			Order order = orderRepository.findByDeletedAtIsNullAndOrderCodeAndUser_Id(orderConfirmRequest.orderCode(), userId)
			                             .orElseThrow(() -> new NotFoundException(Messages.MESSAGE_ORDER_NOTFOUND));
			PaymentStatus targetStatus = orderConfirmRequest.status() ? PaymentStatus.CONFIRMED : PaymentStatus.CANCELLED;
			if (targetStatus == order.getPaymentStatus()) {
				return orderConfirmRequest.status();
			}
			assertOpenOrder(order);
			order.setPaymentStatus(targetStatus);
			orderRepository.save(order);
			readModelSyncService.syncAfterCommit(ReadModelType.ORDER, order.getId());
			return orderConfirmRequest.status();
		} catch (BadRequestException | NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Ensures checkout order can still change state. */
	private void assertOpenOrder(Order order) throws BadRequestException {
		PaymentStatus paymentStatus = order.getPaymentStatus();
		if (paymentStatus != null
				&& paymentStatus != PaymentStatus.NOT_CONFIRMED
				&& paymentStatus != PaymentStatus.ON_HOLD) {
			throw new BadRequestException("messages.orders.locked");
		}
	}
}
