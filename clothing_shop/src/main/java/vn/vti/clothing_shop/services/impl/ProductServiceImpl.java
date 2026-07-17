package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import vn.vti.clothing_shop.constants.Messages;
import vn.vti.clothing_shop.dtos.ins.ProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateRequest;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.ProductMapper;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.repositories.BrandRepository;
import vn.vti.clothing_shop.repositories.CategoryRepository;
import vn.vti.clothing_shop.repositories.ProductRepository;
import vn.vti.clothing_shop.services.interfaces.ProductService;
import vn.vti.clothing_shop.utils.TimeUtils;

import java.util.List;

@Component
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final BrandRepository brandRepository;
	private final CategoryRepository categoryRepository;
	private final ProductMapper productMapper;
	private final MongoReadModelQueryService readModelQueryService;
	private final PostgresToMongoReadModelSyncService readModelSyncService;

	/** Gets all products. */
	@Cacheable(value = "products", key = "'all'")
	@Override
	public List<Product> getAllProducts() {
		List<Product> mongoProducts = readModelQueryService.findAll(ReadModelType.PRODUCT, Product.class);
		if (mongoProducts != null && !mongoProducts.isEmpty()) {
			return mongoProducts;
		}
		return productRepository.findByDeletedAtIsNullOrderByIdDesc();
	}

	/** Adds product. */
	@CacheEvict(value = "products", allEntries = true)
	@Override
	@Transactional
	public void addProduct(ProductCreateRequest productCreateRequest) throws WrapperException {
		try {
			Brand brand = brandRepository
					.findById(productCreateRequest.brandId())
					.orElseThrow(() -> new NotFoundException("messages.brands.notfound"));
			Category category = categoryRepository
					.findById(productCreateRequest.categoryId())
					.orElseThrow(() -> new NotFoundException("messages.categories.notfound"));
			Product product = productRepository.save(productMapper.createRequestToEntity(productCreateRequest, category, brand));
			readModelSyncService.syncAfterCommit(ReadModelType.PRODUCT, product.getId());
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Deletes product. */
	@CacheEvict(value = "products", allEntries = true)
	@Transactional
	@Override
	public void deleteProduct(Long id) throws WrapperException {
		try {
			Product result = productRepository.findById(id).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_PRODUCT_NOTFOUND));
			result.setDeletedAt(TimeUtils.currentEpochMillis());
			productRepository.save(result);
			readModelSyncService.removeAfterCommit(ReadModelType.PRODUCT, id);
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	/** Updates product. */
	@CacheEvict(value = "products", allEntries = true)
	@Transactional
	@Override
	public void updateProduct(ProductUpdateRequest productUpdateRequest, Long productId) throws WrapperException {
		try {
			Product product = productRepository
					.findById(productId)
					.orElseThrow(() -> new NotFoundException(Messages.MESSAGE_PRODUCT_NOTFOUND));
			Brand brand = brandRepository
					.findById(productUpdateRequest.brandId())
					.orElseThrow(() -> new NotFoundException("messages.brands.notfound"));
			Category category = categoryRepository
					.findById(productUpdateRequest.categoryId())
					.orElseThrow(() -> new NotFoundException("messages.categories.notfound"));
			Product savedProduct = productRepository.save(
					productMapper.updateRequestToEntity(productUpdateRequest, category, brand, product));
			readModelSyncService.syncAfterCommit(ReadModelType.PRODUCT, savedProduct.getId());
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	/** Gets product by id. */
	@Cacheable(value = "products", key = "'id:' + #id")
	@Override
	public Product getProductById(Long id) throws WrapperException {
		try {
			var mongoProduct = readModelQueryService.findById(ReadModelType.PRODUCT, id, Product.class);
			if (mongoProduct.isPresent()) {
				return mongoProduct.get();
			}
			return productRepository
					.findById(id)
					.orElseThrow(() -> new NotFoundException(Messages.MESSAGE_PRODUCT_NOTFOUND));
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}
}
