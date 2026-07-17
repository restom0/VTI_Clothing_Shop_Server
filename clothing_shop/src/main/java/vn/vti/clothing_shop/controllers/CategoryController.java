package vn.vti.clothing_shop.controllers;

import io.swagger.v3.oas.annotations.Operation;

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
import vn.vti.clothing_shop.mappers.CategoryMapper;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.CategoryService;

import java.util.List;

@AllArgsConstructor
@RequestMapping("/categories")
@RestController
public class CategoryController {

	private final CategoryService categoryService;
	private final CategoryMapper categoryMapper;

	/** Gets all categories. */
	@Operation(summary = "Gets all categories", description = "Gets all categories API.")
	@GetMapping
	public ResponseEntity<BaseMessageResponse> getAllCategories() {
		List<CategoryDTO> categories = categoryService.getAllCategories()
		                                              .stream()
		                                              .map(categoryMapper::entityToDTO).toList();
		return ResponseHandler.successBuilder(HttpStatus.OK, categories);
	}

	/** Adds category. */
	@Operation(summary = "Adds category", description = "Adds category API.")
	@PostMapping
	public ResponseEntity<BaseMessageResponse> addCategory(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest) {
		try {
			CategoryDTO categoryDTO = categoryMapper.entityToDTO(categoryService.addCategory(categoryCreateRequest));
			return ResponseHandler.successBuilder(HttpStatus.CREATED, categoryDTO);
		} catch (WrapperException exception) {
			return ResponseHandler.exceptionBuilder(exception);
		}
	}

	/** Updates category. */
	@Operation(summary = "Updates category", description = "Updates category API.")
	@PutMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> updateCategory(@RequestBody @Valid CategoryUpdateRequest categoryUpdateRequest,
	                                                          @PathVariable @NotNull(message = "{messages.validation.required}")
	                                                          Long id) {
		try {
			CategoryDTO categoryDTO = categoryMapper.entityToDTO(categoryService.updateCategory(categoryUpdateRequest, id));
			return ResponseHandler.successBuilder(HttpStatus.OK, categoryDTO);
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Deletes category. */
	@Operation(summary = "Deletes category", description = "Deletes category API.")
	@DeleteMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> deleteCategory(
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id) {
		try {
			categoryService.deleteCategory(id);
			return ResponseHandler.successBuilder(HttpStatus.OK, "messages.categories.deleted");
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Gets category by id. */
	@Operation(summary = "Gets category by id", description = "Gets category by id API.")
	@GetMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> getCategoryById(
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id) {
		try {
			CategoryDTO categoryDTO = categoryMapper.entityToDTO(categoryService.getCategoryById(id));
			return ResponseHandler.successBuilder(HttpStatus.OK, categoryDTO);
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}
}
