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
import vn.vti.clothing_shop.dtos.ins.ProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateRequest;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.ProductService;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/product")
public class ProductController {
    private final ProductService productService;

    @GetMapping(value = "/")
    public ResponseEntity<BaseMessageResponse> getAllProducts() {
        return ResponseHandler.successBuilder(HttpStatus.OK, "Lấy danh sách sản phẩm thành công", productService.getAllProducts());
    }

    @PostMapping("/")
    public ResponseEntity<BaseMessageResponse> addProduct(@RequestBody @Valid ProductCreateRequest productCreateRequest) {
        try {
            productService.addProduct(productCreateRequest);
            return ResponseHandler.successBuilder(HttpStatus.CREATED, "Thêm sản phẩm thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> updateProduct(@RequestBody @Valid ProductUpdateRequest productUpdateRequest, @PathVariable @NotNull(message = "Vui lòng chọn sản phẩm") Long id) {
        try {
            productService.updateProduct(productUpdateRequest, id);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Cập nhật sản phẩm thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> deleteProduct(@PathVariable @NotNull(message = "Vui lòng chọn sản phẩm") Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Xóa sản phẩm thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> getProductById(@PathVariable @NotNull(message = "Vui lòng chọn sản phẩm") Long id) {
        try {
            return ResponseHandler.successBuilder(HttpStatus.OK, productService.getProductById(id));
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
