package vn.vti.clothing_shop.controllers;

import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.vti.clothing_shop.mappers.OnSaleProductMapper;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.OnSaleProductService;

@RestController
@RequestMapping("/on-sale-product")
@RequiredArgsConstructor
public class OnSaleProductController {
	private final OnSaleProductService onSaleProductService;
	private final OnSaleProductMapper onSaleProductMapper;

	/** Gets all on sale products. */
	@Operation(summary = "Gets all on sale products", description = "Gets all on sale products API.")
	@GetMapping
	public ResponseEntity<BaseMessageResponse> getAllOnSaleProducts() {
		return ResponseHandler.successBuilder(HttpStatus.OK,
		                                      onSaleProductMapper.entityToDTO(onSaleProductService.getAllOnSaleProducts()));
	}

	/** Gets on sale product by id. */
	@Operation(summary = "Gets on sale product by id", description = "Gets on sale product by id API.")
	@GetMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> getOnSaleProductById(@PathVariable Long id) {
		return ResponseHandler.successBuilder(HttpStatus.OK,
		                                      onSaleProductMapper.entityToDTO(onSaleProductService.getOnSaleProductById(id)));
	}
}
