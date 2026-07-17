package vn.vti.clothing_shop.controllers;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.BrandMapper;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.BrandService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/brands")
public class BrandController {
	private final BrandService brandService;
	private final BrandMapper brandMapper;

	/** Gets brands. */
	@Operation(summary = "Gets brands", description = "Gets brands API.")
	@GetMapping
	public ResponseEntity<BaseMessageResponse> getBrands() {
		final List<BrandDTO> brands = brandService.getBrands().stream()
		                                          .map(brandMapper::entityToDTO)
		                                          .toList();
		return ResponseHandler.successBuilder(
				HttpStatus.OK,
				brands
		);
	}

	/** Adds brand. */
	@Operation(summary = "Adds brand", description = "Adds brand API.")
	@PostMapping("/brand")
	public ResponseEntity<BaseMessageResponse> addBrand(@RequestBody @Valid BrandCreateRequest brandCreateRequest) {
		try {
			final BrandDTO brandDTO =
					brandMapper.entityToDTO(brandService.createBrand(brandCreateRequest));
			return ResponseHandler.successBuilder(
					HttpStatus.CREATED,
					brandDTO
			);
		} catch (WrapperException ex) {
			return ResponseHandler.exceptionBuilder(ex);
		}
	}

	/** Updates brand. */
	@Operation(summary = "Updates brand", description = "Updates brand API.")
	@PatchMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> updateBrand(@RequestBody @Valid BrandUpdateRequest brandUpdateRequest,
	                                                       @PathVariable @NotNull(message = "{messages.validation.required}")
	                                                       Long id) {
		try {
			final BrandDTO updatedBrand = brandMapper.entityToDTO(brandService.updateBrand(brandUpdateRequest, id));
			return ResponseHandler.successBuilder(
					HttpStatus.ACCEPTED,
					updatedBrand
			);
		} catch (WrapperException ex) {
			return ResponseHandler.exceptionBuilder(ex);
		}
	}

	/** Deletes brand. */
	@Operation(summary = "Deletes brand", description = "Deletes brand API.")
	@DeleteMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> deleteBrand(
			@PathVariable @Valid @NotNull(message = "{messages.validation.required}") Long id) {
		try {
			brandService.deleteBrand(id);
			return ResponseHandler.successBuilder(
					HttpStatus.OK,
					"messages.brands.deleted"
			);
		} catch (WrapperException ex) {
			return ResponseHandler.exceptionBuilder(ex);
		}
	}

	/** Gets brand by id. */
	@Operation(summary = "Gets brand by id", description = "Gets brand by id API.")
	@GetMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> getBrandById(
			@PathVariable @Valid @NotNull(message = "{messages.validation.required}") Long id) {
		try {
			final BrandDTO brand = brandMapper.entityToDTO(brandService.findBrandById(id));
			return ResponseHandler.successBuilder(
					HttpStatus.OK,
					brand
			);
		} catch (WrapperException ex) {
			return ResponseHandler.exceptionBuilder(ex);
		}
	}
}
