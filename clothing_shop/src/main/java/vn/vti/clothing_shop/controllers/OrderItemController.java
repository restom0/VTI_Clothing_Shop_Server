package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
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
import vn.vti.clothing_shop.dtos.ins.OrderItemCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderItemUpdateRequest;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.OrderItemMapper;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.impl.OrderItemServiceImpl;

@RestController
@RequestMapping("/order-items")
@AllArgsConstructor
public class OrderItemController {
    private final OrderItemServiceImpl orderItemService;
    private final OrderItemMapper orderItemMapper;

    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getAllOrderItem() {
        return ResponseHandler.successBuilder(HttpStatus.OK, orderItemService.getAllOrderItems());
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<BaseMessageResponse> getAllOrderItemsByOrderId(@PathVariable @NotNull(message = "Vui lòng chọn đơn hàng") Long orderId) {
        return ResponseHandler.successBuilder(HttpStatus.OK, orderItemService.getAllOrderItemsByOrderId(orderId));
    }

    @GetMapping("/{orderId}/{id}")
    public ResponseEntity<BaseMessageResponse> getOrderItemByIdAndOrderId(@PathVariable @NotNull(message = "Vui lòng chọn id") Long id, @PathVariable @NotNull(message = "Vui lòng chọn đơn hàng") Long orderId) {
        try {
            return ResponseHandler.successBuilder(HttpStatus.OK, orderItemService.findOrderItemByIdAndOrderId(id, orderId));
        } catch (WrapperException ex) {
            return ResponseHandler.exceptionBuilder(ex);
        }
    }

    @PostMapping("/")
    public ResponseEntity<BaseMessageResponse> addOrderItem(@RequestBody @Valid @NotNull(message = "Vui lòng nhập thông tin") OrderItemCreateRequest orderItemCreateRequest) {
        try {
            orderItemService.addOrderItem(orderItemCreateRequest);
            return ResponseHandler.successBuilder(HttpStatus.CREATED, "Thêm order item thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{orderId}/{id}")
    public ResponseEntity<BaseMessageResponse> updateOrderItem(@RequestBody @Valid OrderItemUpdateRequest orderItemUpdateRequest
            , @PathVariable @NotNull(message = "Vui lòng chọn đơn hàng") Long orderId
            , @PathVariable @NotNull(message = "Vui lòng chọn id") Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            orderItemService.updateOrderItem(orderItemUpdateRequest, userId, orderId, id);
            return ResponseHandler.successBuilder(HttpStatus.CREATED, "Cập nhật order item thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{orderId}/{id}")
    public ResponseEntity<BaseMessageResponse> deleteOrderItem(
            @PathVariable @NotNull(message = "Vui lòng chọn đơn hàng") Long orderId,
            @PathVariable @NotNull(message = "Vui lòng chọn id") Long id) {
        try {
            orderItemService.deleteOrderItem(id, orderId);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Xóa order item thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
