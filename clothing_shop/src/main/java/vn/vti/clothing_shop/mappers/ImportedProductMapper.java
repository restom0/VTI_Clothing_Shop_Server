package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ImportedProductDTO;
import vn.vti.clothing_shop.entities.Color;
import vn.vti.clothing_shop.entities.ImportedProduct;
import vn.vti.clothing_shop.entities.Material;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.entities.Size;

@Mapper(componentModel = "spring",
        uses = {ProductMapper.class, ColorMapper.class, SizeMapper.class, MaterialMapper.class})
public interface ImportedProductMapper {
    ImportedProductDTO entityToDTO(ImportedProduct importedProduct);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	@Mapping(target = "color", source = "color")
	@Mapping(target = "size", source = "size")
	@Mapping(target = "material", source = "material")
	@Mapping(target = "product", source = "product")
	@Mapping(target = "stock", source = "importedProductCreateRequest.importNumber")
    ImportedProduct createRequestToEntity(ImportedProductCreateRequest importedProductCreateRequest, Color color, Size size, Material material, Product product);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", source = "importedProductUpdateRequest.version")
	@Mapping(target = "color", source = "color")
	@Mapping(target = "size", source = "size")
	@Mapping(target = "material", source = "material")
	@Mapping(target = "product", source = "product")
	@Mapping(target = "stock", source = "importedProductUpdateRequest.importNumber")
    ImportedProduct updateRequestToEntity(ImportedProductUpdateRequest importedProductUpdateRequest, Color color, Size size, Material material, Product product, @MappingTarget ImportedProduct importedProduct);
}
