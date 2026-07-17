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

import vn.vti.clothing_shop.dtos.ins.ProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateRequest;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.ProductMapper;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.ProductService;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/product")
public class ProductController {
	private final ProductService productService;
	private final ProductMapper productMapper;

	/** Gets all products. */
	@Operation(summary = "Gets all products", description = "Gets all products API.")
	@GetMapping
	public ResponseEntity<BaseMessageResponse> getAllProducts() {
		return ResponseHandler.successBuilder(HttpStatus.OK, "messages.products.listFetched",
		                                      productMapper.entityListToDTOList(productService.getAllProducts()));
	}

	/** Adds product. */
	@Operation(summary = "Adds product", description = "Adds product API.")
	@PostMapping
	public ResponseEntity<BaseMessageResponse> addProduct(@RequestBody @Valid ProductCreateRequest productCreateRequest) {
		try {
			productService.addProduct(productCreateRequest);
			return ResponseHandler.successBuilder(HttpStatus.CREATED, "messages.products.created");
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Updates product. */
	@Operation(summary = "Updates product", description = "Updates product API.")
	@PutMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> updateProduct(@RequestBody @Valid ProductUpdateRequest productUpdateRequest,
	                                                         @PathVariable @NotNull(message = "{messages.validation.required}")
	                                                         Long id) {
		try {
			productService.updateProduct(productUpdateRequest, id);
			return ResponseHandler.successBuilder(HttpStatus.OK, "messages.products.updated");
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Deletes product. */
	@Operation(summary = "Deletes product", description = "Deletes product API.")
	@DeleteMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> deleteProduct(
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id) {
		try {
			productService.deleteProduct(id);
			return ResponseHandler.successBuilder(HttpStatus.OK, "messages.products.deleted");
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Gets product by id. */
	@Operation(summary = "Gets product by id", description = "Gets product by id API.")
	@GetMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> getProductById(
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id) {
		try {
			return ResponseHandler.successBuilder(HttpStatus.OK, productMapper.entityToDTO(productService.getProductById(id)));
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}
}
