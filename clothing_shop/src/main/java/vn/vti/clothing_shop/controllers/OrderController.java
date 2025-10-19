package vn.vti.clothing_shop.controllers;

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
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
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

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {
    private static final String REDIRECT_URL = "localhost";
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getAllOrders() {
        return ResponseHandler.successBuilder(HttpStatus.OK, orderService.getAllOrders());
    }

    @GetMapping("/user")
    public ResponseEntity<BaseMessageResponse> getAllOrdersByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return ResponseHandler.successBuilder(HttpStatus.OK, orderService.getAllOrdersByUserId(userId));
    }

    @GetMapping("/cart")
    public ResponseEntity<BaseMessageResponse> addOrder() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            OrderDTO orderDTO = orderService.addOrder(null, userId);
            return ResponseHandler.successBuilder(HttpStatus.OK, orderDTO);
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> updateOrder(
            @RequestBody @NotNull(message = "Vui lòng nhập thông tin đơn hàng") OrderUpdateRequest orderUpdateRequest,
            @PathVariable @NotNull(message = "Vui lòng chọn giỏ hàng") Long id) {
        try {
            orderService.updateOrder(id, orderUpdateRequest);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Cập nhật đơn hàng thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{orderId}/{id}")
    public ResponseEntity<BaseMessageResponse> deleteOrder(
            @PathVariable @NotNull(message = "Vui lòng chọn đơn hàng") Long orderId,
            @PathVariable @NotNull(message = "Vui lòng chọn giỏ hàng") Long id) {
        try {
            orderItemService.deleteOrderItem(id, orderId);
            orderService.deleteOrder(orderId);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Xóa đơn hàng thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PostMapping(path = "/checkout")
    public ResponseEntity<BaseMessageResponse> createPaymentLink(@RequestBody @NotNull(message = "Vui lòng chọn đơn hàng") OrderCheckoutRequest orderCheckoutRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            OrderDTO orderDTO = orderService.getOrderByIdAndUserId(orderCheckoutRequest, userId);
            List<ItemData> itemDataList = orderDTO.
                    getOrderItems()
                    .stream()
                    .map(orderItemDTO -> ItemData
                            .builder()
                            .name(orderItemDTO.getProduct().getProduct().getProduct().getName())
                            .quantity(orderItemDTO.getQuantity()).build())
                    .toList();
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderDTO.getOrderCode())
                    .items(itemDataList)
                    .amount(orderDTO.getTotalPrice().intValue())
                    .returnUrl(REDIRECT_URL + "success")
                    .cancelUrl(REDIRECT_URL + "cancel")
                    .build();
            return ResponseHandler.successBuilder(HttpStatus.OK, "Tạo link thanh toán thành công", paymentData);
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/success")
    public ResponseEntity<BaseMessageResponse> success(@RequestBody @NotNull(message = "Vui lòng chọn đơn hàng") OrderConfirmRequest orderConfirmRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            return ResponseHandler.successBuilder(HttpStatus.OK, orderService.confirmOrder(orderConfirmRequest, userId));
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/cancel")
    public ResponseEntity<BaseMessageResponse> cancel(@RequestBody @NotNull(message = "Vui lòng chọn đơn hàng") OrderConfirmRequest orderConfirmRequest) {
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
