package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateRequest;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.InputSaleService;

@AllArgsConstructor
@RestController
@RequestMapping("/input-sale")
public class InputSaleController {
    private final InputSaleService inputSaleService;

    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getAllInputSale() {
        return ResponseHandler.successBuilder(HttpStatus.OK, inputSaleService.getAllInputSale());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> getInputSaleById(@PathVariable @NotNull(message = "Vui lòng nhập id") Long id) {
        try {
            return ResponseHandler.successBuilder(HttpStatus.OK, inputSaleService.getInputSaleById(id));
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PostMapping("/")
    public ResponseEntity<BaseMessageResponse> createInputSale(@RequestBody @Valid @NotNull(message = "Vui lòng nhập thông tin sản phẩm nhập") InputSaleCreateRequest inputSaleCreateRequest) {
        inputSaleService.createInputSale(inputSaleCreateRequest);
        return ResponseHandler.successBuilder(HttpStatus.CREATED, "Thêm sản phẩm nhập thành công");
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> updateInputSale(@PathVariable @NotNull(message = "Vui lòng nhập id") Long id, @RequestBody @Valid @NotNull(message = "Vui lòng nhập thông tin sản phẩm nhập") InputSaleUpdateRequest inputSaleUpdateRequest) {
        try {
            inputSaleService.updateInputSale(inputSaleUpdateRequest, id);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Cập nhật sản phẩm nhập thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> deleteInputSale(@PathVariable @NotNull(message = "Vui lòng nhập id") Long id) {
        inputSaleService.deleteInputSale(id);
        return ResponseHandler.successBuilder(HttpStatus.OK, "Xóa sản phẩm nhập thành công");
    }
}
