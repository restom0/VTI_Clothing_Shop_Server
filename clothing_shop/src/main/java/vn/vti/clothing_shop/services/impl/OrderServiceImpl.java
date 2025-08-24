package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.vti.clothing_shop.constants.PaymentStatus;
import vn.vti.clothing_shop.dtos.ins.OrderCheckoutRequest;
import vn.vti.clothing_shop.dtos.ins.OrderConfirmRequest;
import vn.vti.clothing_shop.dtos.ins.OrderCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.exceptions.ForbiddenException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.OrderMapper;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.repositories.VoucherRepository;
import vn.vti.clothing_shop.services.interfaces.OrderService;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final PayOS payOS;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final OrderMapper orderMapper;

    //@Cacheable(value = "orders")
    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findByDeletedAtIsNullOrderByIdDesc()
                .stream()
                .map(orderMapper::entityToDTO)
                .toList();
    }

    //@Cacheable(value = "orders", key = "#userId")
    @Override
    public List<OrderDTO> getAllOrdersByUserId(Long userId) {
        return orderRepository.findByDeletedAtIsNullAndUser_Id(userId)
                .stream()
                .map(orderMapper::entityToDTO)
                .toList();
    }

    //@Cacheable(value = "orders", key = "#id,#userId")
    @Override
    public OrderDTO getOrderByIdAndUserId(Long id, Long userId) throws WrapperException {
        try {
            Order order = orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(id, userId).orElseThrow(() -> new NotFoundException("Order not found"));
            return orderMapper.entityToDTO(order);
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    //@CacheEvict(value = "orders", allEntries = true)
    @Transactional
    @Override
    public OrderDTO addOrder(OrderCreateRequest orderCreateRequest, Long userId) throws WrapperException {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new ForbiddenException("User not found"));
            Order order = orderRepository.findByDeletedAtIsNullAndUser_IdAndPaymentStatus(userId, PaymentStatus.NOT_CONFIRMED).orElse(orderRepository.save(orderMapper.createRequestToEntity(orderCreateRequest, user)));
            return orderMapper.entityToDTO(order);
        } catch (ForbiddenException ex) {
            throw new WrapperException(ex);
        }
    }

    private void adjustStock(Long id, Integer quantity) throws WrapperException {
        try {
            Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new NotFoundException("Voucher not found"));
            if (voucher.getStock() < 0
                    || voucher.getEndDate() < Instant.now().toEpochMilli()
                    || voucher.getAvailableDate() > Instant.now().toEpochMilli()) {
                throw new BadRequestException("Voucher out of stock");
            }
            voucher.setStock(voucher.getStock() + quantity);
            voucherRepository.save(voucher);
        } catch (NotFoundException | BadRequestException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CachePut(value = "orders")
    @Transactional
    public void updateOrder(Long id, OrderUpdateRequest orderUpdateRequest) throws WrapperException {
        try {
            Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
            Voucher voucher = voucherRepository.findById(orderUpdateRequest.voucherId()).orElseThrow(() -> new NotFoundException("Voucher not found"));
            adjustStock(voucher.getId(), NumberUtils.INTEGER_MINUS_ONE);
            orderRepository.save(orderMapper.updateRequestToEntity(orderUpdateRequest, voucher, order));
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CacheEvict(value = "orders", allEntries = true)
    @Transactional
    @Override
    public void deleteOrder(Long id) throws WrapperException {
        try {
            Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order not found"));
            adjustStock(order.getVoucher().getId(), NumberUtils.INTEGER_ONE);
            order.setPaymentStatus(PaymentStatus.CANCELLED);
            order.setDeletedAt(Instant.now().toEpochMilli());
            orderRepository.save(order);
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    @Override
    public OrderDTO getOrderByIdAndUserId(OrderCheckoutRequest orderCheckoutRequest, Long userId) throws WrapperException {
        try {
            Order order = orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(orderCheckoutRequest.orderId(), userId).orElseThrow(() -> new NotFoundException("Order not found"));
            return orderMapper.entityToDTO(order);
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    @Transactional
    @Override
    public Boolean confirmOrder(OrderConfirmRequest orderConfirmRequest, Long userId) throws WrapperException {
        try {
            Order order = orderRepository.findByDeletedAtIsNullAndOrderCodeAndUser_Id(orderConfirmRequest.orderCode(), userId).orElseThrow(() -> new NotFoundException("Order not found"));
            order.setPaymentStatus(orderConfirmRequest.status() ? PaymentStatus.CONFIRMED : PaymentStatus.CANCELLED);
            orderRepository.save(order);
            return orderConfirmRequest.status();
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }
}
