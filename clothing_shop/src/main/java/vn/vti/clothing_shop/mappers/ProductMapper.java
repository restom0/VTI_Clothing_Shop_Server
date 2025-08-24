package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.ProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ProductDTO;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Product;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, BrandMapper.class})
public interface ProductMapper {

    ProductDTO entityToDTO(Product product);

    List<ProductDTO> entityListToDTOList(List<Product> products);

    Product createRequestToEntity(ProductCreateRequest productCreateRequest, Category category, Brand brand);

    Product updateRequestToEntity(ProductUpdateRequest productUpdateRequest, Category category, Brand brand, @MappingTarget Product product);

}