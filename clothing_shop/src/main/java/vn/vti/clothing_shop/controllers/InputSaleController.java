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

import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateRequest;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.InputSaleMapper;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.InputSaleService;

@AllArgsConstructor
@RestController
@RequestMapping("/input-sale")
public class InputSaleController {
	private final InputSaleService inputSaleService;
	private final InputSaleMapper inputSaleMapper;

	/** Gets all input sale. */
	@Operation(summary = "Gets all input sale", description = "Gets all input sale API.")
	@GetMapping
	public ResponseEntity<BaseMessageResponse> getAllInputSale() {
		return ResponseHandler.successBuilder(HttpStatus.OK, inputSaleService.getAllInputSale().stream()
		                                                                     .map(inputSaleMapper::entityToDTO)
		                                                                     .toList());
	}

	/** Gets input sale by id. */
	@Operation(summary = "Gets input sale by id", description = "Gets input sale by id API.")
	@GetMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> getInputSaleById(
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id) {
		try {
			return ResponseHandler.successBuilder(HttpStatus.OK,
			                                      inputSaleMapper.entityToDTO(inputSaleService.getInputSaleById(id)));
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Creates input sale. */
	@Operation(summary = "Creates input sale", description = "Creates input sale API.")
	@PostMapping
	public ResponseEntity<BaseMessageResponse> createInputSale(
			@RequestBody @Valid @NotNull(message = "{messages.validation.required}")
			InputSaleCreateRequest inputSaleCreateRequest) {
		inputSaleService.createInputSale(inputSaleCreateRequest);
		return ResponseHandler.successBuilder(HttpStatus.CREATED, "messages.inputSales.created");
	}

	/** Updates input sale. */
	@Operation(summary = "Updates input sale", description = "Updates input sale API.")
	@PutMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> updateInputSale(
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id,
			@RequestBody @Valid @NotNull(message = "{messages.validation.required}")
			InputSaleUpdateRequest inputSaleUpdateRequest) {
		try {
			inputSaleService.updateInputSale(inputSaleUpdateRequest, id);
			return ResponseHandler.successBuilder(HttpStatus.OK, "messages.inputSales.updated");
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Deletes input sale. */
	@Operation(summary = "Deletes input sale", description = "Deletes input sale API.")
	@DeleteMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> deleteInputSale(
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id) {
		inputSaleService.deleteInputSale(id);
		return ResponseHandler.successBuilder(HttpStatus.OK, "messages.inputSales.deleted");
	}
}
