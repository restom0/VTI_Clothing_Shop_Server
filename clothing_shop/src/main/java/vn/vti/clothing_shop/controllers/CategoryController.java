package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import vn.vti.clothing_shop.mappers.CategoryMapper;
import vn.vti.clothing_shop.requests.CategoryCreateRequest;
import vn.vti.clothing_shop.requests.CategoryUpdateRequest;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.exceptions.InternalServerErrorException;
import vn.vti.clothing_shop.services.implementations.CategoryServiceImplementation;
import vn.vti.clothing_shop.utils.ParameterUtils;

import java.util.HashMap;
import java.util.Map;
@RequestMapping("/category")
@RestController
public class CategoryController {

    private final CategoryServiceImplementation categoryServiceImplementation;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryController(CategoryServiceImplementation categoryServiceImplementation, CategoryMapper categoryMapper) {
        this.categoryServiceImplementation = categoryServiceImplementation;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllCategories(){
        try{
            return ResponseHandler.responseBuilder(200,"Lấy danh sách danh mục thành công",categoryServiceImplementation.getAllCategories(), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> addCategory(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ParameterUtils.showBindingResult(bindingResult);
        }
        try {
            if(categoryServiceImplementation.addCategory(categoryMapper.CategoryCreateRequestToCategoryCreateDTO(categoryCreateRequest))){
                return ResponseHandler.responseBuilder(201,"Thêm danh mục thành công",null, HttpStatus.CREATED);
            }
            throw new InternalServerErrorException("Thêm danh mục thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest, @PathVariable @NotNull(message = "Vui lòng chọn category") Long id, BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors()){
                return ParameterUtils.showBindingResult(bindingResult);
            }
            if(categoryServiceImplementation.updateCategory(categoryMapper.CategoryUpdateRequestToCategoryUpdateDTO(categoryUpdateRequest,id))){
                return ResponseHandler.responseBuilder(200,"Cập nhật danh mục thành công",null, HttpStatus.OK);
            }
            throw new InternalServerErrorException("Cập nhật danh mục thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable @NotNull(message = "Vui lòng chọn category") Long id){
        try {
            if(categoryServiceImplementation.deleteCategory(id)){
                return ResponseHandler.responseBuilder(200,"Xóa danh mục thành công",null, HttpStatus.OK);
            }
            throw new InternalServerErrorException("Xóa danh mục thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable @NotNull(message = "Vui lòng chọn brand") Long id){
        try {
            return ResponseHandler.responseBuilder(200,"Lấy thông tin thương hiệu thành công",categoryServiceImplementation.getCategoryById(id), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
