package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.InternalServerErrorException;
import vn.vti.clothing_shop.mappers.OrderItemMapper;
import vn.vti.clothing_shop.requests.OrderItemCreateRequest;
import vn.vti.clothing_shop.requests.OrderItemUpdateRequest;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.implementations.OrderItemServiceImplementation;
import vn.vti.clothing_shop.utils.ParameterUtils;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {
    private final OrderItemServiceImplementation orderItemService;
    private final OrderItemMapper orderItemMapper;

    @Autowired
    public OrderItemController(OrderItemServiceImplementation orderItemService, OrderItemMapper orderItemMapper) {
        this.orderItemService = orderItemService;
        this.orderItemMapper = orderItemMapper;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllOrderItem() {
        try{
            return ResponseHandler.responseBuilder( 200,"Lấy danh sách order item thành công", orderItemService.getAllOrderItem(), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getAllOrderItemsByOrderId(@PathVariable @NotNull(message = "Vui lòng chọn đơn hàng") Long orderId) {
        try{
            return ResponseHandler.responseBuilder( 200,"Lấy danh sách order item thành công", orderItemService.getAllOrderItemsByOrderId(orderId), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/{orderId}/{id}")
    public ResponseEntity<?> getOrderItemByIdAndOrderId(@PathVariable @NotNull(message = "Vui lòng chọn id") Long id, @PathVariable @NotNull(message = "Vui lòng chọn đơn hàng") Long orderId) {
        try{
            return ResponseHandler.responseBuilder( 200,"Lấy order item thành công", orderItemService.getOrderItemByIdAndOrderId(id,orderId), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addOrderItem(@RequestBody @Valid @NotNull(message = "Vui lòng nhập thông tin") OrderItemCreateRequest orderItemCreateRequest, BindingResult bindingResult) {
        try{
            if(bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            if(orderItemService.addOrderItem(orderItemMapper.CreateRequestToCreateDTO(orderItemCreateRequest)))
                return ResponseHandler.responseBuilder(201, "Thêm order item thành công", null, HttpStatus.CREATED);
            throw new InternalServerErrorException("Thêm order item thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> updateOrderItem(@RequestBody @Valid @NotNull(message = "Vui lòng nhập thông tin")OrderItemUpdateRequest orderItemUpdateRequest,BindingResult bindingResult) {
        try{
            if(bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            if(orderItemService
                    .updateOrderItem(orderItemMapper
                            .UpdateRequestToUpdateDTO(orderItemUpdateRequest, userId))) {
                return ResponseHandler.responseBuilder(201, "Cập nhật order item thành công", null, HttpStatus.CREATED);
            }
            throw new InternalServerErrorException("Cập nhật order item thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{orderId}/{id}")
    public ResponseEntity<?> deleteOrderItem(
            @PathVariable @NotNull(message = "Vui lòng chọn đơn hàng") Long orderId,
            @PathVariable @NotNull(message = "Vui lòng chọn id") Long id) {
        try{
            if(orderItemService.deleteOrderItem(id,orderId))
                return ResponseHandler.responseBuilder(200, "Xóa order item thành công", null, HttpStatus.OK);
            throw new InternalServerErrorException("Xóa order item thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
