package vn.vti.clothing_shop.services.implementations;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.ProductCreateDTO;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.ProductDTO;
import vn.vti.clothing_shop.mappers.BrandMapper;
import vn.vti.clothing_shop.mappers.CategoryMapper;
import vn.vti.clothing_shop.mappers.ProductMapper;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.exceptions.InternalServerErrorException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.repositories.BrandRepository;
import vn.vti.clothing_shop.repositories.CategoryRepository;
import vn.vti.clothing_shop.repositories.ProductRepository;
import vn.vti.clothing_shop.services.interfaces.ProductService;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProductServiceImplementation implements ProductService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;

    @Autowired
    public ProductServiceImplementation(ProductRepository productRepository, BrandRepository brandRepository, CategoryRepository categoryRepository, ProductMapper productMapper, CategoryMapper categoryMapper, BrandMapper brandMapper) {
        this.productRepository = productRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.brandMapper = brandMapper;
    }

    //@Cacheable(value = "products")
    public List<ProductDTO> getAllProducts(){
        return this.productMapper.EntityListToDTOList(this.productRepository.findAll());
    };

    //@CacheEvict(value = "products", allEntries = true)
    @Transactional
    public Boolean addProduct(ProductCreateDTO productCreateDTO){
        Brand brand =this.brandRepository
                .findById(productCreateDTO.getBrand_id())
                .orElseThrow(()->new NotFoundException("Brand not found"));
        Category category = this.categoryRepository
                .findById(productCreateDTO.getCategory_id())
                .orElseThrow(()->new NotFoundException("Category not found"));
        this.productRepository.save(productMapper.ProductCreateDTOToEntity(productCreateDTO,category,brand));
        return true;
    };

    //@CacheEvict(value = "products", allEntries = true)
    @Transactional
    public Boolean deleteProduct(Long id){
        try{
            Product result = this.productRepository.findById(id).orElseThrow(()->new NotFoundException("Product not found"));
            result.setDeleted_at(LocalDateTime.now());
            return true;
        }
        catch (Exception e){
            throw new InternalServerErrorException("Server error");
        }
    };

    //@CachePut(value = "products")
    public Boolean updateProduct(ProductUpdateDTO productUpdateDTO){
        try{
            Product product = this.productRepository
                    .findById(productUpdateDTO.getId())
                    .orElseThrow(()->new NotFoundException("Product not found"));
            Brand brand =this.brandRepository
                    .findById(productUpdateDTO.getBrand_id())
                    .orElseThrow(()->new NotFoundException("Brand not found"));
            Category category = this.categoryRepository
                    .findById(productUpdateDTO.getBrand_id())
                    .orElseThrow(()->new NotFoundException("Category not found"));
            this.productRepository.save(productMapper.ProductUpdateDTOToEntity(productUpdateDTO,category,brand,product));
            return true;
        }
        catch (Exception e){
            throw new InternalServerErrorException("Server error");
        }
    };

    //@Cacheable(value = "products", key = "#id")
    public ProductDTO getProductById(Long id){
        try{
            return productMapper
                    .EntityToDTO(
                            this.productRepository
                                    .findById(id)
                                    .orElseThrow(()->new NotFoundException("Product not found"))
                    );
        }
        catch (Exception e){
            throw new InternalServerErrorException("Server error");
        }
    };
}
