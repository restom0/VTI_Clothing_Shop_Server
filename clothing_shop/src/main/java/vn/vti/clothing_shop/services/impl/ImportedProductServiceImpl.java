package vn.vti.clothing_shop.services.impl;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import vn.vti.clothing_shop.constants.Filter;
import vn.vti.clothing_shop.constants.Messages;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
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

	@Caching(evict = {
			@CacheEvict(value = "importedProducts", allEntries = true),
			@CacheEvict(value = "colors", allEntries = true),
			@CacheEvict(value = "materials", allEntries = true),
			@CacheEvict(value = "sizes", allEntries = true)
	})
	@Transactional
	@Override
	public void addImportedProduct(ImportedProductCreateRequest request) throws WrapperException {
		try {
			final Long productId = request.productId();
			final String colorCode = request.code();
			final String sizeName = request.size();
			final String height = request.height();
			final String weight = request.weight();
			final String materialName = request.material();

			// Fetch the product once
			final Product product = productRepository
					.findByIdAndDeletedAtIsNull(productId)
					.orElseThrow(() -> new NotFoundException("messages.products.notfound"));

			// Fetch or create a color
			final Color color = colorRepository
					.findByDeletedAtIsNullAndCode(colorCode)
					.orElseGet(() -> {
						Color newColor = colorMapper.createRequestToEntity(request, product.getCategory());
						return colorRepository.save(newColor);
					});

			final Size size = sizeRepository
					.findByDeletedAtIsNullAndNameAndHeightAndWeight(sizeName, height, weight)
					.orElseGet(() -> {
						Size newSize = sizeMapper.createRequestToEntity(request, product.getCategory());
						return sizeRepository.save(newSize);
					});

			final Material material = materialRepository
					.findByDeletedAtIsNullAndName(materialName)
					.orElseGet(() -> {
						Material newMaterial = materialMapper.createRequestToEntity(request, product.getCategory());
						return materialRepository.save(newMaterial);
					});

			final ImportedProduct importedProduct = importedProductMapper
					.createRequestToEntity(request, color, size, material, product);
			importedProductRepository.save(importedProduct);

		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	@CacheEvict(value = "importedProducts", allEntries = true)
	@Transactional
	@Override
	public void deleteImportedProduct(Long id) throws WrapperException {
		try {
			ImportedProduct importedProduct = importedProductRepository
					.findById(id)
					.orElseThrow(() -> new NotFoundException(Messages.MESSAGE_IMPORTED_PRODUCT_NOTFOUND));
			importedProductRepository.delete(importedProduct);
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	@Caching(evict = {
			@CacheEvict(value = "importedProducts", allEntries = true),
			@CacheEvict(value = "colors", allEntries = true),
			@CacheEvict(value = "materials", allEntries = true),
			@CacheEvict(value = "sizes", allEntries = true)
	})
	@Transactional
	@Override
	public void updateImportedProduct(Long id, ImportedProductUpdateRequest importedProductUpdateRequest)
			throws WrapperException {
		try {
			ImportedProduct importedProduct = importedProductRepository
					.findById(id)
					.orElseThrow(() -> new NotFoundException(Messages.MESSAGE_IMPORTED_PRODUCT_NOTFOUND));
			final Product product = productRepository
					.findById(importedProductUpdateRequest.productId())
					.orElseThrow(() -> new NotFoundException("messages.products.notfound"));
			final Color color = colorRepository
					.findById(importedProductUpdateRequest.colorId())
					.orElseThrow(() -> new NotFoundException("messages.colors.notfound"));
			final Size size = sizeRepository
					.findById(importedProductUpdateRequest.sizeId())
					.orElseThrow(() -> new NotFoundException("messages.sizes.notfound"));
			final Material material = materialRepository
					.findById(importedProductUpdateRequest.materialId())
					.orElseThrow(() -> new NotFoundException("messages.materials.notfound"));

			final Color newColor = colorRepository.save(colorMapper
					                                            .updateRequestToEntity(importedProductUpdateRequest,
					                                                                   product.getCategory(), color));

			final Size newSize = sizeRepository.save(sizeMapper
					                                         .updateRequestToEntity(importedProductUpdateRequest,
					                                                                product.getCategory(), size));

			final Material newMaterial = materialRepository.save(materialMapper
					                                                     .updateRequestToEntity(importedProductUpdateRequest,
					                                                                            product.getCategory(), material));

			importedProductRepository.save(
					importedProductMapper.updateRequestToEntity(importedProductUpdateRequest, newColor, newSize, newMaterial,
					                                            product, importedProduct));
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	@Cacheable(value = "importedProducts", key = "'id:' + #id")
	@Override
	public ImportedProduct findImportedProductById(Long id) throws WrapperException {
		try {
			return importedProductRepository.findById(id)
			                                .orElseThrow(() -> new NotFoundException(
					                                Messages.MESSAGE_IMPORTED_PRODUCT_NOTFOUND));
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	@Cacheable(value = "importedProducts", key = "'filter:' + #filter + ':id:' + #id")
	@Override
	public List<ImportedProduct> getImportedProductByFilter(Filter filter, Long id) {
		return switch (filter) {
			case ALL -> getAllImportedProducts();
			case PRODUCT -> importedProductRepository
					.findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO);
			case CATEGORY -> importedProductRepository
					.findByDeletedAtIsNullAndProduct_Category_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO);
			case BRAND -> importedProductRepository
					.findByDeletedAtIsNullAndProduct_Brand_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO);
			case COLOR -> importedProductRepository
					.findByDeletedAtIsNullAndColor_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO);
			case SIZE -> importedProductRepository
					.findByDeletedAtIsNullAndSize_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO);
			case MATERIAL -> importedProductRepository
					.findByDeletedAtIsNullAndMaterial_IdAndStockGreaterThan(id, NumberUtils.INTEGER_ZERO);
		};
	}

	@Cacheable(value = "importedProducts", key = "'all'")
	@Override
	public List<ImportedProduct> getAllImportedProducts() {
		return importedProductRepository.findAll();
	}

	@Cacheable(value = "colors", key = "'all'")
	@Override
	public List<Color> getColors() {
		return colorRepository.findByDeletedAtIsNull();
	}

	@Cacheable(value = "materials", key = "'all'")
	@Override
	public List<Material> getMaterials() {
		return materialRepository.findByDeletedAtIsNull();
	}

	@Cacheable(value = "sizes", key = "'all'")
	@Override
	public List<Size> getSizes() {
		return sizeRepository.findByDeletedAtIsNull();
	}
}
