package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.vti.clothing_shop.dtos.ins.ProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ProductDTO;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Product;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = { CategoryMapper.class, BrandMapper.class })
public interface ProductMapper {

	ProductDTO entityToDTO(Product product);

	List<ProductDTO> entityListToDTOList(List<Product> products);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "name", source = "productCreateRequest.name")
	@Mapping(target = "shortDescription", source = "productCreateRequest.shortDescription")
	@Mapping(target = "category", source = "category")
	@Mapping(target = "brand", source = "brand")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	Product createRequestToEntity(ProductCreateRequest productCreateRequest, Category category, Brand brand);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "name", source = "productUpdateRequest.name")
	@Mapping(target = "shortDescription", source = "productUpdateRequest.shortDescription")
	@Mapping(target = "category", source = "category")
	@Mapping(target = "brand", source = "brand")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", source = "productUpdateRequest.version")
	Product updateRequestToEntity(ProductUpdateRequest productUpdateRequest, Category category, Brand brand,
	                              @MappingTarget Product product);

}
