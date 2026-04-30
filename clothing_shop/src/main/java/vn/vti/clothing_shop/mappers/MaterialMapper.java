package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.MaterialDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Material;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface MaterialMapper {
    MaterialDTO entityToDTO(Material material);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "importedProductCreateRequest.material")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Material createRequestToEntity(ImportedProductCreateRequest importedProductCreateRequest, Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "importedProductUpdateRequest.material")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Material updateRequestToEntity(ImportedProductUpdateRequest importedProductUpdateRequest, Category category, @MappingTarget Material material);
}
