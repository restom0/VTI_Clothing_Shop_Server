package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateRequest;
import vn.vti.clothing_shop.entities.InputSale;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface InputSaleService {
	/** Gets all input sale. */
	List<InputSale> getAllInputSale();

	/** Gets input sale by id. */
	InputSale getInputSaleById(Long id) throws WrapperException;

	/** Creates input sale. */
	void createInputSale(InputSaleCreateRequest inputSaleCreateRequest);

	/** Updates input sale. */
	void updateInputSale(InputSaleUpdateRequest inputSaleUpdateRequest, Long inputSaleId) throws WrapperException;

	/** Deletes input sale. */
	void deleteInputSale(Long id);
}
