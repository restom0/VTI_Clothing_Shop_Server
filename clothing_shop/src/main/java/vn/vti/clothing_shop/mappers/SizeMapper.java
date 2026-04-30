package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.SizeDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Size;

import java.util.List;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface SizeMapper {

    @Mapping(target = "size", source = "name")
    SizeDTO entityToDTO(Size size);

    List<SizeDTO> listEntityToDTO(List<Size> sizes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "importedProductCreateRequest.size")
    @Mapping(target = "height", source = "importedProductCreateRequest.height")
    @Mapping(target = "weight", source = "importedProductCreateRequest.weight")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Size createRequestToEntity(ImportedProductCreateRequest importedProductCreateRequest, Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "importedProductUpdateRequest.size")
    @Mapping(target = "height", source = "importedProductUpdateRequest.height")
    @Mapping(target = "weight", source = "importedProductUpdateRequest.weight")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Size updateRequestToEntity(ImportedProductUpdateRequest importedProductUpdateRequest, Category category, @MappingTarget Size size);
}
