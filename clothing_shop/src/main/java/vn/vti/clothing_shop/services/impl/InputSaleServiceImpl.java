package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.constants.Messages;
import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateRequest;
import vn.vti.clothing_shop.entities.ImportedProduct;
import vn.vti.clothing_shop.entities.InputSale;
import vn.vti.clothing_shop.entities.OnSaleProduct;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.InputSaleMapper;
import vn.vti.clothing_shop.mappers.OnSaleProductMapper;
import vn.vti.clothing_shop.repositories.ImportedProductRepository;
import vn.vti.clothing_shop.repositories.InputSaleRepository;
import vn.vti.clothing_shop.repositories.OnSaleProductRepository;
import vn.vti.clothing_shop.services.interfaces.InputSaleService;
import vn.vti.clothing_shop.utils.TimeUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class InputSaleServiceImpl implements InputSaleService {
	private final InputSaleRepository inputSaleRepository;
	private final OnSaleProductRepository onSaleProductRepository;
	private final ImportedProductRepository importedProductRepository;
	private final InputSaleMapper inputSaleMapper;
	private final OnSaleProductMapper onSaleProductMapper;

	/** Gets all input sale. */
	@Cacheable(value = "inputSales", key = "'all'")
	@Override
	public List<InputSale> getAllInputSale() {
		return inputSaleRepository.findByDeletedAtIsNull();
	}

	/** Gets input sale by id. */
	@Cacheable(value = "inputSales", key = "'id:' + #id")
	@Override
	public InputSale getInputSaleById(Long id) throws WrapperException {
		try {
			return inputSaleRepository.findById(id).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_INPUT_SALE_NOTFOUND));
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	/** Creates input sale. */
	@Caching(evict = {
			@CacheEvict(value = "inputSales", allEntries = true),
			@CacheEvict(value = "onSaleProducts", allEntries = true)
	})
	@Transactional
	@Override
	public void createInputSale(InputSaleCreateRequest inputSaleCreateRequest) {
		InputSale inputSale = inputSaleMapper.createRequestEntity(inputSaleCreateRequest);
		inputSaleRepository.save(inputSale);
		switch (inputSaleCreateRequest.filter()) {
			case ALL:
				saveListOnSaleProduct(
						importedProductRepository.findByDeletedAtIsNullAndStockGreaterThan(NumberUtils.INTEGER_ZERO), inputSale);
				break;
			case PRODUCT:
				saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(
						inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
				break;
			case BRAND:
				saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndProduct_Brand_IdAndStockGreaterThan(
						inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
				break;
			case CATEGORY:
				saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndProduct_Category_IdAndStockGreaterThan(
						inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
				break;
			case COLOR:
				saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndColor_IdAndStockGreaterThan(
						inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
				break;
			case SIZE:
				saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndSize_IdAndStockGreaterThan(
						inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
				break;
			case MATERIAL:
				saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndMaterial_IdAndStockGreaterThan(
						inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
				break;
		}
	}

	/** Saves list on sale product. */
	private void saveListOnSaleProduct(List<ImportedProduct> importedProducts, InputSale inputSale) {
		if (importedProducts.isEmpty()) {
			return;
		}
		importedProducts.forEach(importedProduct -> {
			if (isValidToSave(importedProduct, inputSale)) {
				return;
			}
			OnSaleProduct onSaleProduct = onSaleProductMapper.importProductAndInputSaleToOnSaleProduct(importedProduct,
			                                                                                           inputSale);
			onSaleProductRepository.save(onSaleProduct);
		});
	}

	/** Checks whether valid to save. */
	private boolean isValidToSave(ImportedProduct importedProduct, InputSale inputSale) {
		Long productId = importedProduct.getId();
		LocalDate startDate = inputSale.getStartDate();
		LocalDate endDate = inputSale.getEndDate();

		if (endDate != null) {
			return onSaleProductRepository
					.findByProductIdAndAvailableDateAndNotNullEndDate(productId, startDate, endDate)
					.isPresent();
		} else {
			return onSaleProductRepository
					.findByProductIdAndAvailableDateAndNullEndDate(productId, startDate)
					.map(onSaleProduct -> {
						if (onSaleProduct.getInputSale().getStartDate().isBefore(startDate)) {
							onSaleProduct.getInputSale().setEndDate(TimeUtils.today());
							onSaleProductRepository.save(onSaleProduct);
						}
						// An open sale already covers this product on this date; a second one
						// would leave two competing open sales.
						return true;
					})
					// Nothing on sale for this product yet, so the new sale must be created.
					.orElse(false);
		}
	}

	/** Updates input sale. */
	@Caching(evict = {
			@CacheEvict(value = "inputSales", allEntries = true),
			@CacheEvict(value = "onSaleProducts", allEntries = true)
	})
	@Transactional
	@Override
	public void updateInputSale(InputSaleUpdateRequest inputSaleUpdateRequest, Long inputSaleId) throws WrapperException {
		try {
			InputSale inputSale = inputSaleRepository.findById(inputSaleId).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_INPUT_SALE_NOTFOUND));
			List<OnSaleProduct> onSaleProducts
					= onSaleProductRepository.findByInputSale_IdAndInputSale_StartDateLessThanEqualAndDeletedAtIsNullAndInputSale_DeletedAtIsNullOrderByIdDesc(
					inputSaleId, TimeUtils.today());
			// Apply the requested values first: repricing must use the new percentage/discount,
			// not the ones the sale had before this update.
			InputSale updatedInputSale = inputSaleMapper.updateRequestToEntity(inputSaleUpdateRequest, inputSale);
			updateListOnSaleProduct(onSaleProducts, updatedInputSale);
			inputSaleRepository.save(updatedInputSale);
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	/** Updates list on sale product. */
	private void updateListOnSaleProduct(List<OnSaleProduct> onSaleProducts, InputSale inputSale) {
		if (onSaleProducts.isEmpty()) {
			return;
		}
		onSaleProducts.forEach(onSaleProduct -> {
			if (isValidToSave(onSaleProduct.getProduct(), inputSale)) {
				return;
			}
			onSaleProduct.setSalePrice(Math.round(onSaleProduct.getSalePrice() * inputSale.getSalePercentage() / 100.0));
			onSaleProduct.getInputSale().setDiscount(inputSale.getDiscount());
			onSaleProductRepository.save(onSaleProduct);
		});
	}

	/** Deletes input sale. */
	@Caching(evict = {
			@CacheEvict(value = "inputSales", allEntries = true),
			@CacheEvict(value = "onSaleProducts", allEntries = true)
	})
	@Transactional
	@Override
	public void deleteInputSale(Long id) {
		InputSale inputSale = inputSaleRepository.findById(id).orElseThrow(
				() -> new RuntimeException(Messages.MESSAGE_INPUT_SALE_NOTFOUND));
		List<OnSaleProduct> onSaleProducts =
				onSaleProductRepository.findByInputSale_IdAndInputSale_StartDateLessThanEqualAndDeletedAtIsNullAndInputSale_DeletedAtIsNullOrderByIdDesc(
						id, TimeUtils.today());

		onSaleProducts.forEach(
				onSaleProduct -> onSaleProduct.setDeletedAt(TimeUtils.currentEpochMillis())
		);
		onSaleProductRepository.saveAll(onSaleProducts);
		inputSale.setDeletedAt(TimeUtils.currentEpochMillis());
		inputSaleRepository.save(inputSale);
	}
}
