package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.vti.clothing_shop.mappers.ProductMapper;
import vn.vti.clothing_shop.requests.ProductCreateRequest;
import vn.vti.clothing_shop.requests.ProductUpdateRequest;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.exceptions.InternalServerErrorException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.services.implementations.ProductServiceImplementation;
import vn.vti.clothing_shop.utils.ParameterUtils;

@RestController
@RequestMapping(value = "/product")
public class ProductController {

    private final ProductServiceImplementation productServiceImplementation;
    private final ProductMapper productMapper;

    @Autowired
    public ProductController(ProductServiceImplementation productServiceImplementation, ProductMapper productMapper) {
        this.productServiceImplementation = productServiceImplementation;
        this.productMapper = productMapper;
    }

    @GetMapping(value="/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAllProducts(){
        try {

            return ResponseHandler.responseBuilder(200,"Lấy danh sách sản phẩm thành công",productServiceImplementation.getAllProducts(), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addProduct(@RequestBody @Valid ProductCreateRequest productCreateRequest, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()) {
                return ParameterUtils.showBindingResult(bindingResult);
            }
            if(productServiceImplementation.addProduct(productMapper.ProductCreateRequestToProductCreateDTO(productCreateRequest)))
                return ResponseHandler.responseBuilder(201, "Thêm sản phẩm thành công", null, HttpStatus.CREATED);
            throw new InternalServerErrorException("Thêm sản phẩm thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid ProductUpdateRequest productUpdateRequest, @PathVariable @NotNull(message = "Vui lòng chọn sản phẩm") Long id,BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()) {
                return ParameterUtils.showBindingResult(bindingResult);
            }
            if(productServiceImplementation.updateProduct(productMapper.ProductUpdateRequestToProductUpdateDTO(id,productUpdateRequest)))
                return ResponseHandler.responseBuilder(200,"Cập nhật sản phẩm thành công",null, HttpStatus.OK);

            throw new InternalServerErrorException("Cập nhật sản phẩm thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable @NotNull(message = "Vui lòng chọn sản phẩm") Long id){
        try {
            if(productServiceImplementation.deleteProduct(id)){
                return ResponseHandler.responseBuilder(200,"Xóa sản phẩm thành công",null, HttpStatus.OK);
            }
            throw new InternalServerErrorException("Xóa sản phẩm thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable @NotNull(message = "Vui lòng chọn sản phẩm") Long id){
        try {
                return ResponseHandler.responseBuilder(200,"Lấy sản phẩm thành công",productServiceImplementation.getProductById(id), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
