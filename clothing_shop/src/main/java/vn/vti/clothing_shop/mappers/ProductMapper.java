package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.ProductCreateDTO;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.ProductDTO;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.requests.ProductCreateRequest;
import vn.vti.clothing_shop.requests.ProductUpdateRequest;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    private final ModelMapper modelMapper;
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;

    @Autowired
    public ProductMapper(ModelMapper modelMapper, CategoryMapper categoryMapper, BrandMapper brandMapper) {
        this.modelMapper = modelMapper;
        this.categoryMapper = categoryMapper;
        this.brandMapper = brandMapper;
    }

    public ProductDTO EntityToDTO(Product product) {
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        productDTO.setCategory_id(categoryMapper.EntityToDTO(product.getCategory_id()));
        productDTO.setBrand_id(brandMapper.EntityToDTO(product.getBrand_id()));
        return productDTO;
    }

    public List<ProductDTO> EntityListToDTOList(List<Product> products){
        return products.stream().map(this::EntityToDTO).collect(Collectors.toList());
    }

    public ProductCreateDTO ProductCreateRequestToProductCreateDTO(ProductCreateRequest productCreateRequest){
        return modelMapper.map(productCreateRequest, ProductCreateDTO.class);
    }

    public Product ProductCreateDTOToEntity(ProductCreateDTO productCreateDTO, Category category, Brand brand){
        Product product= modelMapper.map(productCreateDTO, Product.class);
        product.setCategory_id(category);
        product.setBrand_id(brand);
        return product;
    }

    public ProductUpdateDTO ProductUpdateRequestToProductUpdateDTO(Long id, ProductUpdateRequest productUpdateRequest){
        ProductUpdateDTO productUpdateDTO = modelMapper.map(productUpdateRequest, ProductUpdateDTO.class);
        productUpdateDTO.setId(id);
        return productUpdateDTO;
    }
    public Product ProductUpdateDTOToEntity(ProductUpdateDTO productUpdateDTO,Category category, Brand brand, Product product){
        product.setName(productUpdateDTO.getName());
        product.setShort_description(productUpdateDTO.getShort_description());
        product.setBrand_id(brand);
        product.setCategory_id(category);
        return product;
    }
}
