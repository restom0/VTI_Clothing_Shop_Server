package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.InputSaleDTO;
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

import java.time.Instant;
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

    //@Cacheable(value = "inputSales")
    @Override
    public List<InputSaleDTO> getAllInputSale() {
        return inputSaleRepository.findByDeletedAtIsNull()
                .stream()
                .map(inputSaleMapper::entityToDTO)
                .toList();
    }

    //@Cacheable(value = "inputSales", key = "#id")
    @Override
    public InputSaleDTO getInputSaleById(Long id) throws WrapperException {
        try {
            InputSale inputSale = inputSaleRepository.findById(id).orElseThrow(() -> new NotFoundException("InputSale not found"));
            return inputSaleMapper.entityToDTO(inputSale);
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

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
                            onSaleProduct.getInputSale().setEndDate(LocalDate.now());
                            onSaleProductRepository.save(onSaleProduct);
                            return true;
                        }
                        return false;
                    })
                    .orElse(true);
        }
    }

    private void saveListOnSaleProduct(List<ImportedProduct> importedProducts, InputSale inputSale) {
        if (importedProducts.isEmpty()) return;
        importedProducts.forEach(importedProduct -> {
            if (isValidToSave(importedProduct, inputSale)) return;
            OnSaleProduct onSaleProduct = onSaleProductMapper.importProductAndInputSaleToOnSaleProduct(importedProduct, inputSale);
            onSaleProductRepository.save(onSaleProduct);
        });
    }

    //@CacheEvict(value = "inputSales", allEntries = true)
    @Transactional
    @Override
    public void createInputSale(InputSaleCreateRequest inputSaleCreateRequest) {
        InputSale inputSale = inputSaleMapper.createRequestEntity(inputSaleCreateRequest);
        inputSaleRepository.save(inputSale);
        switch (inputSaleCreateRequest.filter()) {
            case ALL:
                saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndStockGreaterThan(NumberUtils.INTEGER_ZERO), inputSale);
                break;
            case PRODUCT:
                saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
                break;
            case BRAND:
                saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndBrand_IdAndStockGreaterThan(inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
                break;
            case CATEGORY:
                saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndCategory_IdAndStockGreaterThan(inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
                break;
            case COLOR:
                saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndColor_IdAndStockGreaterThan(inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
                break;
            case SIZE:
                saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndSize_IdAndStockGreaterThan(inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
                break;
            case MATERIAL:
                saveListOnSaleProduct(importedProductRepository.findByDeletedAtIsNullAndMaterial_IdAndStockGreaterThan(inputSaleCreateRequest.filterId(), NumberUtils.INTEGER_ZERO), inputSale);
                break;
        }
    }

    private void updateListOnSaleProduct(List<OnSaleProduct> onSaleProducts, InputSale inputSale) {
        if (onSaleProducts.isEmpty()) return;
        onSaleProducts.forEach(onSaleProduct -> {
            if (isValidToSave(onSaleProduct.getProduct(), inputSale)) return;
            onSaleProduct.setSalePrice(onSaleProduct.getSalePrice() * inputSale.getSalePercentage() / 100);
            onSaleProduct.getInputSale().setDiscount(inputSale.getDiscount());
            onSaleProductRepository.save(onSaleProduct);
        });
    }

    //@CachePut(value = "inputSales")
    @Transactional
    @Override
    public void updateInputSale(InputSaleUpdateRequest inputSaleUpdateRequest, Long inputSaleId) throws WrapperException {
        try {
            InputSale inputSale = inputSaleRepository.findById(inputSaleId).orElseThrow(() -> new NotFoundException("InputSale not found"));
            List<OnSaleProduct> onSaleProducts = onSaleProductRepository.findByInputSale_IdAndInputSale_StartDateLessThanEqualAndDeletedAtIsNullAndInputSale_DeletedAtIsNullOrderByIdDesc(inputSaleId, LocalDate.now());
            updateListOnSaleProduct(onSaleProducts, inputSale);
            inputSaleRepository.save(inputSaleMapper.updateRequestToEntity(inputSaleUpdateRequest, inputSale));
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    //@CacheEvict(value = "inputSales", allEntries = true)
    @Transactional
    @Override
    public void deleteInputSale(Long id) {
        InputSale inputSale = inputSaleRepository.findById(id).orElseThrow(() -> new RuntimeException("InputSale not found"));
        List<OnSaleProduct> onSaleProducts =
                onSaleProductRepository.findByInputSale_IdAndInputSale_StartDateLessThanEqualAndDeletedAtIsNullAndInputSale_DeletedAtIsNullOrderByIdDesc(id, LocalDate.now());

        onSaleProducts.forEach(
                onSaleProduct -> onSaleProduct.setDeletedAt(Instant.now().toEpochMilli())
        );
        onSaleProductRepository.saveAll(onSaleProducts);
        inputSale.setDeletedAt(Instant.now().toEpochMilli());
        inputSaleRepository.save(inputSale);
    }
}
