package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
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


    //@Cacheable(value = "products")
    public List<ProductDTO> getAllProducts() {
        return productRepository.findByDeletedAtIsNullOrderByIdDesc().stream()
                .map(productMapper::entityToDTO)
                .toList();
    }

    //@CacheEvict(value = "products", allEntries = true)
    @Transactional
    public void addProduct(ProductCreateRequest productCreateRequest) throws WrapperException {
        try {
            Brand brand = brandRepository
                    .findById(productCreateRequest.brandId())
                    .orElseThrow(() -> new NotFoundException("Brand not found"));
            Category category = categoryRepository
                    .findById(productCreateRequest.categoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            productRepository.save(productMapper.createRequestToEntity(productCreateRequest, category, brand));
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CacheEvict(value = "products", allEntries = true)
    @Transactional
    public void deleteProduct(Long id) throws WrapperException {
        try {
            Product result = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found"));
            result.setDeletedAt(Instant.now().toEpochMilli());
            productRepository.save(result);
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    //@CachePut(value = "products")
    public void updateProduct(ProductUpdateRequest productUpdateRequest, Long productId) throws WrapperException {
        try {
            Product product = productRepository
                    .findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            Brand brand = brandRepository
                    .findById(productUpdateRequest.brandId())
                    .orElseThrow(() -> new NotFoundException("Brand not found"));
            Category category = categoryRepository
                    .findById(productUpdateRequest.categoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
            productRepository.save(productMapper.updateRequestToEntity(productUpdateRequest, category, brand, product));
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }

    //@Cacheable(value = "products", key = "#id")
    public ProductDTO getProductById(Long id) throws WrapperException {
        try {
            Product product = productRepository
                    .findById(id)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            return productMapper.entityToDTO(product);
        } catch (NotFoundException e) {
            throw new WrapperException(e);
        }
    }
}
