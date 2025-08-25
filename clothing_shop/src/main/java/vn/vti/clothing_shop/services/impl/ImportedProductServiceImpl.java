package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.constants.Filter;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ColorDTO;
import vn.vti.clothing_shop.dtos.outs.ImportedProductDTO;
import vn.vti.clothing_shop.dtos.outs.MaterialDTO;
import vn.vti.clothing_shop.dtos.outs.SizeDTO;
import vn.vti.clothing_shop.entities.Color;
import vn.vti.clothing_shop.entities.ImportedProduct;
import vn.vti.clothing_shop.entities.Material;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.entities.Size;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.ColorMapper;
import vn.vti.clothing_shop.mappers.ImportedProductMapper;
import vn.vti.clothing_shop.mappers.MaterialMapper;
import vn.vti.clothing_shop.mappers.SizeMapper;
import vn.vti.clothing_shop.repositories.ColorRepository;
import vn.vti.clothing_shop.repositories.ImportedProductRepository;
import vn.vti.clothing_shop.repositories.MaterialRepository;
import vn.vti.clothing_shop.repositories.ProductRepository;
import vn.vti.clothing_shop.repositories.SizeRepository;
import vn.vti.clothing_shop.services.interfaces.ImportedProductService;

import java.util.List;

@Service
@AllArgsConstructor
public class ImportedProductServiceImpl implements ImportedProductService {

    private final ImportedProductRepository importedProductRepository;
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final MaterialRepository materialRepository;
    private final ImportedProductMapper importedProductMapper;
    private final ColorMapper colorMapper;
    private final SizeMapper sizeMapper;
    private final MaterialMapper materialMapper;

    //@Cacheable(value = "importedProducts")
    @Override
    public List<ImportedProductDTO> getAllImportedProducts() {
        return importedProductRepository
                .findAll()
                .stream()
                .map(importedProductMapper::entityToDTO)
                .toList();
    }

    //@CacheEvict(value = "importedProducts", allEntries = true)
    @Transactional
    @Override
    public void addImportedProduct(ImportedProductCreateRequest importedProductCreateRequest) throws WrapperException {
        try {
            final Product product = productRepository
                    .findByIdAndDeletedAtIsNull(importedProductCreateRequest.productId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            final Color color = colorRepository
                    .findByDeletedAtIsNullAndCode(importedProductCreateRequest.code())
                    .orElseGet(() -> {
                        Color newColor = colorMapper.createRequestToEntity(importedProductCreateRequest, product.getCategory());
                        return colorRepository.save(newColor);
                    });
            final Size size = sizeRepository
                    .findByDeletedAtIsNullAndSizeAndHeightAndWeight(importedProductCreateRequest.size(), importedProductCreateRequest.height(), importedProductCreateRequest.weight())
                    .orElseGet(() -> {
                        Size newSize = sizeMapper.createRequestToEntity(importedProductCreateRequest, product.getCategory());
                        return sizeRepository.save(newSize);
                    });
            final Material material = materialRepository
                    .findByDeletedAtIsNullAndName(importedProductCreateRequest.material())
                    .orElseGet(() -> {
                        Material newMaterial = materialMapper.createRequestToEntity(importedProductCreateRequest, product.getCategory());
                        return materialRepository.save(newMaterial);
                    });
            importedProductRepository.save(importedProductMapper.createRequestToEntity(importedProductCreateRequest, color, size, material, product));
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CacheEvict(value = "importedProducts", allEntries = true)
    @Transactional
    @Override
    public void deleteImportedProduct(Long id) throws WrapperException {
        try {
            ImportedProduct importedProduct = importedProductRepository
                    .findById(id)
                    .orElseThrow(() -> new NotFoundException("Imported product not found"));
            importedProductRepository.delete(importedProduct);
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    //@CachePut(value = "importedProducts")
    @Transactional
    @Override
    public void updateImportedProduct(Long id, ImportedProductUpdateRequest importedProductUpdateRequest) throws WrapperException {
        try {
            ImportedProduct importedProduct = importedProductRepository
                    .findById(id)
                    .orElseThrow(() -> new NotFoundException("Imported product not found"));
            final Product product = productRepository
                    .findById(importedProductUpdateRequest.productId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            final Color color = colorRepository
                    .findById(importedProductUpdateRequest.colorId())
                    .orElseThrow(() -> new NotFoundException("Color not found"));
            final Size size = sizeRepository
                    .findById(importedProductUpdateRequest.sizeId())
                    .orElseThrow(() -> new NotFoundException("Size not found"));
            final Material material = materialRepository
                    .findById(importedProductUpdateRequest.materialId())
                    .orElseThrow(() -> new NotFoundException("Material not found"));

            final Color newColor = colorRepository.save(colorMapper
                    .updateRequestToEntity(importedProductUpdateRequest, product.getCategory(), color));

            final Size newSize = sizeRepository.save(sizeMapper
                    .updateRequestToEntity(importedProductUpdateRequest, product.getCategory(), size));

            final Material newMaterial = materialRepository.save(materialMapper
                    .updateRequestToEntity(importedProductUpdateRequest, product.getCategory(), material));


            importedProductRepository.save(importedProductMapper.updateRequestToEntity(importedProductUpdateRequest, newColor, newSize, newMaterial, product, importedProduct));
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    //@Cacheable(value = "importedProducts", key = "#id")
    @Override
    public ImportedProductDTO findImportedProductById(Long id) throws WrapperException {
        try {
            return importedProductMapper.entityToDTO(importedProductRepository.findById(id).orElseThrow(() -> new NotFoundException("Imported product not found")));
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    //@Cacheable(value = "importedProducts", key = "#filter, #id")
    @Override
    public List<ImportedProductDTO> getImportedProductByFilter(Filter filter, Long id) {
        return switch (filter) {
            case ALL -> getAllImportedProducts();
            case PRODUCT -> importedProductRepository
                    .findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO)
                    .stream()
                    .map(importedProductMapper::entityToDTO)
                    .toList();
            case CATEGORY -> importedProductRepository
                    .findByDeletedAtIsNullAndCategory_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO)
                    .stream()
                    .map(importedProductMapper::entityToDTO)
                    .toList();
            case BRAND -> importedProductRepository
                    .findByDeletedAtIsNullAndBrand_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO)
                    .stream()
                    .map(importedProductMapper::entityToDTO)
                    .toList();
            case COLOR -> importedProductRepository
                    .findByDeletedAtIsNullAndColor_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO)
                    .stream()
                    .map(importedProductMapper::entityToDTO)
                    .toList();
            case SIZE -> importedProductRepository
                    .findByDeletedAtIsNullAndSize_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO)
                    .stream()
                    .map(importedProductMapper::entityToDTO)
                    .toList();
            case MATERIAL -> importedProductRepository
                    .findByDeletedAtIsNullAndMaterial_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO)
                    .stream()
                    .map(importedProductMapper::entityToDTO)
                    .toList();
        };
    }

    //@Cacheable(value = "colors")
    @Override
    public List<ColorDTO> getColors() {
        return colorRepository.findByDeletedAtIsNull().stream()
                .map(colorMapper::entityToDTO)
                .toList();
    }

    //@Cacheable(value = "materials")
    @Override
    public List<MaterialDTO> getMaterials() {
        return materialRepository.findByDeletedAtIsNull()
                .stream()
                .map(materialMapper::entityToDTO)
                .toList();
    }

    //@Cacheable(value = "sizes")
    @Override
    public List<SizeDTO> getSizes() {
        return sizeRepository.findByDeletedAtIsNull()
                .stream()
                .map(sizeMapper::entityToDTO)
                .toList();
    }
}
