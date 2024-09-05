package vn.vti.clothing_shop.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.InternalServerErrorException;
import vn.vti.clothing_shop.mappers.OrderMapper;
import vn.vti.clothing_shop.requests.OrderCheckoutRequest;
import vn.vti.clothing_shop.requests.OrderConfirmRequest;
import vn.vti.clothing_shop.requests.OrderCreateRequest;
import vn.vti.clothing_shop.requests.OrderUpdateRequest;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.implementations.OrderItemServiceImplementation;
import vn.vti.clothing_shop.services.implementations.OrderServiceImplementation;
import vn.vti.clothing_shop.utils.ParameterUtils;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final String redirectUrl;
    private final OrderServiceImplementation orderServiceImplementation;
    private final OrderItemServiceImplementation orderItemServiceImplementation;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(Environment env, OrderServiceImplementation orderServiceImplementation, OrderItemServiceImplementation orderItemServiceImplementation, OrderMapper orderMapper) {
        this.redirectUrl = env.getProperty("redirectUrl");
        this.orderServiceImplementation = orderServiceImplementation;
        this.orderItemServiceImplementation = orderItemServiceImplementation;
        this.orderMapper = orderMapper;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllOrders() {
        try{
            return ResponseHandler.responseBuilder(200,"Lấy đơn hàng thành công",orderServiceImplementation.getAllOrders(), HttpStatus.OK);
        }
        catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAllOrdersByUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            return ResponseHandler.responseBuilder(200,"Lấy đơn hàng thành công",orderServiceImplementation.getAllOrdersByUserId(userId), HttpStatus.OK);
        }
        catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @GetMapping("/cart")
    public ResponseEntity<?> addOrder() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            return ResponseHandler.responseBuilder(200,"Thêm đơn hàng thành công",this.orderServiceImplementation.addOrder(orderMapper.CreateRequestToCreateDTO(new OrderCreateRequest(),userId)), HttpStatus.CREATED);
        }
        catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(
            @RequestBody @NotNull(message = "Vui lòng nhập thông tin đơn hàng") OrderUpdateRequest orderUpdateRequest,
            @PathVariable @NotNull(message = "Vui lòng chọn giỏ hàng") Long id,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            if(orderServiceImplementation.updateOrder(orderMapper.UpdateRequestToUpdateDTO(orderUpdateRequest,id)))
                return ResponseHandler.responseBuilder(200,"Cập nhật đơn hàng thành công",null, HttpStatus.OK);
            throw new InternalServerErrorException("Cập nhật đơn hàng thất bại");
        }
        catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @DeleteMapping("/{orderId}/{id}")
    public ResponseEntity<?> deleteOrder(
            @PathVariable @NotNull(message = "Vui lòng chọn đơn hàng") Long orderId,
            @PathVariable @NotNull(message = "Vui lòng chọn giỏ hàng") Long id) {
        try {
            if(orderServiceImplementation.deleteOrder(id) &&
                    orderItemServiceImplementation.deleteOrderItem(id,orderId))
                return ResponseHandler.responseBuilder(200,"Xóa đơn hàng thành công",null, HttpStatus.OK);
            throw new InternalServerErrorException("Xóa đơn hàng thất bại");
        }
        catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @PostMapping(path = "/checkout")
    public ResponseEntity<?> createPaymentLink(@RequestBody @NotNull(message = "Vui lòng chọn đơn hàng") OrderCheckoutRequest orderCheckoutRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            OrderDTO orderDTO = orderServiceImplementation.getOrderByIdAndUserId(orderMapper.CheckoutRequestToCheckoutDTO(orderCheckoutRequest,userId));
            List<ItemData> itemDataList = orderDTO.
                    getOrderItems()
                    .stream()
                    .map(orderItemDTO -> ItemData
                            .builder()
                            .name(orderItemDTO.getProduct_id().getProduct_id().getProduct_id().getName())
                            .quantity(orderItemDTO.getQuantity()).build())
                    .toList();
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderDTO.getOrder_code())
                    .items(itemDataList)
                    .amount(orderDTO.getTotal_price().intValue())
                    .returnUrl(redirectUrl+"success")
                    .cancelUrl(redirectUrl+"cancel")
                    .build();
            return ResponseHandler.responseBuilder(200,"Tạo link thanh toán thành công",paymentData, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @PutMapping("/success")
    public ResponseEntity<?> success(@RequestBody @NotNull(message = "Vui lòng chọn đơn hàng") OrderConfirmRequest orderConfirmRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            return ResponseHandler.responseBuilder(200, "Thanh toán thành công", this.orderServiceImplementation.confirmOrder(this.orderMapper.ConfirmRequestToConfirmDTO(orderConfirmRequest,userId,true)), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @PutMapping("/cancel")
    public ResponseEntity<?> cancel(@RequestBody @NotNull(message = "Vui lòng chọn đơn hàng") OrderConfirmRequest orderConfirmRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            return ResponseHandler.responseBuilder(200, "Thanh toán thành công", this.orderServiceImplementation.confirmOrder(this.orderMapper.ConfirmRequestToConfirmDTO(orderConfirmRequest,userId,true)), HttpStatus.OK);
        }
        catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
