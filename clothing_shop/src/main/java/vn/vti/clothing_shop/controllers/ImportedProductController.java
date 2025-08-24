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
import vn.vti.clothing_shop.constants.Filter;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.ImportedProductService;

@AllArgsConstructor
@RestController
@RequestMapping("/imported-product")
public class ImportedProductController {

    private final ImportedProductService importedProductService;

    @GetMapping(value = "/")
    public ResponseEntity<BaseMessageResponse> getAllImportedProducts() {
        return ResponseHandler.successBuilder(HttpStatus.OK, importedProductService.getAllImportedProducts());
    }

    @PostMapping("/")
    public ResponseEntity<BaseMessageResponse> addImportedProduct(
            @RequestBody
            @Valid
            ImportedProductCreateRequest importedProductCreateRequest) {
        try {
            importedProductService.addImportedProduct(importedProductCreateRequest);
            return ResponseHandler.successBuilder(HttpStatus.CREATED, "Thêm sản phẩm nhập khẩu thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> updateImportedProduct(
            @RequestBody
            @Valid
            ImportedProductUpdateRequest importedProductUpdateRequest,
            @PathVariable
            @NotNull(message = "Vui lòng chọn sản phẩm")
            Long id) {
        try {
            importedProductService.updateImportedProduct(id, importedProductUpdateRequest);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Cập nhật sản phẩm nhập khẩu thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> deleteImportedProduct(
            @PathVariable
            @NotNull(message = "Vui lòng chọn sản phẩm")
            Long id) {
        try {
            importedProductService.deleteImportedProduct(id);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Xóa sản phẩm nhập khẩu thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/{filter}/{id}")
    public ResponseEntity<BaseMessageResponse> getImportedProductById(
            @PathVariable
            @NotNull(message = "Vui lòng chọn bộ lọc")
            Filter filter,
            @PathVariable
            @NotNull(message = "Vui lòng chọn sản phẩm") Long id) {
        try {
            return ResponseHandler.successBuilder(HttpStatus.OK, "Lấy sản phẩm nhập khẩu thành công", importedProductService.getImportedProductByFilter(filter, id));
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/colors")
    public ResponseEntity<BaseMessageResponse> getColors() {
        return ResponseHandler.successBuilder(HttpStatus.OK, importedProductService.getColors());
    }

    @GetMapping("/materials")
    public ResponseEntity<BaseMessageResponse> getMaterials() {
        return ResponseHandler.successBuilder(HttpStatus.OK, importedProductService.getMaterials());
    }

    @GetMapping("/sizes")
    public ResponseEntity<BaseMessageResponse> getSizes() {
        return ResponseHandler.successBuilder(HttpStatus.OK, importedProductService.getSizes());
    }
}
