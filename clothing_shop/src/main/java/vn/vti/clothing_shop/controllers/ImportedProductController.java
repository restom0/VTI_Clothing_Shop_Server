package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.vti.clothing_shop.constants.Filter;
import vn.vti.clothing_shop.mappers.ImportedProductMapper;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.implementations.ImportedProductService;
import vn.vti.clothing_shop.utils.ParameterUtils;

@RestController
@RequestMapping("/imported-product")
public class ImportedProductController {

    private final ImportedProductService importedProductService;
    private final ImportedProductMapper importedProductMapper;

    @Autowired
    public ImportedProductController(ImportedProductService importedProductService, ImportedProductMapper importedProductMapper) {
        this.importedProductService = importedProductService;
        this.importedProductMapper = importedProductMapper;
    }

    @GetMapping(value = "/",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllImportedProducts(){
        try{
            return ResponseHandler.responseBuilder(200,"Lấy danh sách sản phẩm nhập khẩu thành công", importedProductService.getAllImportedProducts(), HttpStatus.OK);
            }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PostMapping ("/")
    public ResponseEntity<?> addImportedProduct(
            @RequestBody
            @Valid
            ImportedProductCreateRequest importedProductCreateRequest,
            BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()) {
                return ParameterUtils.showBindingResult(bindingResult);
            }
            if(importedProductService
                    .addImportedProduct(importedProductMapper
                            .ImportedProductCreateRequestToImportedProductCreateDTO(importedProductCreateRequest))){
                return ResponseHandler.responseBuilder(201,"Thêm sản phẩm nhập khẩu thành công",null, HttpStatus.CREATED);
            }
            throw new InternalServerErrorException("Thêm sản phẩm nhập khẩu thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateImportedProduct(
            @RequestBody
            @Valid
            ImportedProductUpdateRequest importedProductUpdateRequest,
            @PathVariable
            @NotNull(message = "Vui lòng chọn sản phẩm")
            Long id,
            @org.jetbrains.annotations.NotNull
            BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()) {
                return ParameterUtils.showBindingResult(bindingResult);
            }

            if(importedProductService
                    .updateImportedProduct(importedProductMapper
                            .ImportedProductUpdateRequestToImportedProductUpdateDTO(importedProductUpdateRequest,id))){
                return ResponseHandler.responseBuilder(200,"Cập nhật sản phẩm nhập khẩu thành công",null, HttpStatus.OK);
            }
            throw new InternalServerErrorException("Cập nhật sản phẩm nhập khẩu thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImportedProduct(
            @PathVariable
            @NotNull(message = "Vui lòng chọn sản phẩm")
            Long id){
        try {
            if(importedProductService.deleteImportedProduct(id)){
                return ResponseHandler.responseBuilder(200,"Xóa sản phẩm nhập khẩu thành công",null, HttpStatus.OK);
            }
            throw new InternalServerErrorException("Xóa sản phẩm nhập khẩu thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/{filter}/{id}")
    public ResponseEntity<?> getImportedProductById(
            @PathVariable
            @NotNull(message = "Vui lòng chọn bộ lọc")
            Filter filter,
            @PathVariable
            @NotNull(message = "Vui lòng chọn sản phẩm") Long id){
        try {
            return ResponseHandler.responseBuilder(200,"Lấy sản phẩm nhập khẩu thành công", importedProductService.getImportedProductByFilter(filter,id), HttpStatus.OK);
            }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @GetMapping("/colors")
    public ResponseEntity<?> getColors(){
        try {
            return ResponseHandler.responseBuilder(200,"Lấy danh sách màu thành công", importedProductService.getColors(), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/materials")
    public ResponseEntity<?> getMaterials(){
        try {
            return ResponseHandler.responseBuilder(200,"Lấy danh sách chất liệu thành công", importedProductService.getMaterials(), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/sizes")
    public ResponseEntity<?> getSizes(){
        try {
            return ResponseHandler.responseBuilder(200,"Lấy danh sách kích cỡ thành công", importedProductService.getSizes(), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
