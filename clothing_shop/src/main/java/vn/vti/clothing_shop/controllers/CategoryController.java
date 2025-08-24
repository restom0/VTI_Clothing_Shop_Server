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
import vn.vti.clothing_shop.dtos.ins.CategoryCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.CategoryService;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseHandler.successBuilder(HttpStatus.OK, categories);
    }

    @PostMapping("/")
    public ResponseEntity<BaseMessageResponse> addCategory(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest) {
        try {
            CategoryDTO categoryDTO = categoryService.addCategory(categoryCreateRequest);
            return ResponseHandler.successBuilder(HttpStatus.CREATED, categoryDTO);
        } catch (WrapperException exception) {
            return ResponseHandler.exceptionBuilder(exception);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> updateCategory(@RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest, @PathVariable @NotNull(message = "Vui lòng chọn category") Long id) {
        try {
            CategoryDTO categoryDTO = categoryService.updateCategory(categoryUpdateRequest, id);
            return ResponseHandler.successBuilder(HttpStatus.OK, categoryDTO);
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> deleteCategory(@PathVariable @NotNull(message = "Vui lòng chọn category") Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Xóa danh mục thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> getCategoryById(@PathVariable @NotNull(message = "Vui lòng chọn brand") Long id) {
        try {
            CategoryDTO categoryDTO = categoryService.getCategoryById(id);
            return ResponseHandler.successBuilder(HttpStatus.OK, categoryDTO);
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
