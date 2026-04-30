package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.ProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ProductDTO;
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

import java.time.Instant;
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


    @Cacheable(value = "products", key = "'all'")
    @Override
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> mongoProducts = readModelQueryService.findAll(ReadModelType.PRODUCT, ProductDTO.class);
        if (mongoProducts != null && !mongoProducts.isEmpty()) {
            return mongoProducts;
        }
        return productRepository.findByDeletedAtIsNullOrderByIdDesc().stream()
                .map(productMapper::entityToDTO)
                .toList();
    }

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

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    @Override
    public void deleteProduct(Long id) throws WrapperException {
        try {
            Product result = productRepository.findById(id).orElseThrow(() -> new NotFoundException("messages.products.notfound"));
            result.setDeletedAt(Instant.now().toEpochMilli());
            productRepository.save(result);
            readModelSyncService.removeAfterCommit(ReadModelType.PRODUCT, id);
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    @CacheEvict(value = "products", allEntries = true)
    @Transactional
    @Override
    public void updateProduct(ProductUpdateRequest productUpdateRequest, Long productId) throws WrapperException {
        try {
            Product product = productRepository
                    .findById(productId)
                    .orElseThrow(() -> new NotFoundException("messages.products.notfound"));
            Brand brand = brandRepository
                    .findById(productUpdateRequest.brandId())
                    .orElseThrow(() -> new NotFoundException("messages.brands.notfound"));
            Category category = categoryRepository
                    .findById(productUpdateRequest.categoryId())
                    .orElseThrow(() -> new NotFoundException("messages.categories.notfound"));
            Product savedProduct = productRepository.save(productMapper.updateRequestToEntity(productUpdateRequest, category, brand, product));
            readModelSyncService.syncAfterCommit(ReadModelType.PRODUCT, savedProduct.getId());
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    @Cacheable(value = "products", key = "'id:' + #id")
    @Override
    public ProductDTO getProductById(Long id) throws WrapperException {
        try {
            var mongoProduct = readModelQueryService.findById(ReadModelType.PRODUCT, id, ProductDTO.class);
            if (mongoProduct != null && mongoProduct.isPresent()) {
                return mongoProduct.get();
            }
            Product product = productRepository
                    .findById(id)
                    .orElseThrow(() -> new NotFoundException("messages.products.notfound"));
            return productMapper.entityToDTO(product);
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }
}
