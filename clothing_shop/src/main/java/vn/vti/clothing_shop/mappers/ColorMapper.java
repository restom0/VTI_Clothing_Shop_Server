package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ColorDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Color;

import java.util.List;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ColorMapper {
    ColorDTO entityToDTO(Color color);

    List<ColorDTO> entityToDTO(List<Color> colors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", source = "importedProductCreateRequest.code")
    @Mapping(target = "name", source = "importedProductCreateRequest.name")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Color createRequestToEntity(ImportedProductCreateRequest importedProductCreateRequest, Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", source = "importedProductUpdateRequest.code")
    @Mapping(target = "name", source = "importedProductUpdateRequest.name")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Color updateRequestToEntity(ImportedProductUpdateRequest importedProductUpdateRequest, Category category, @MappingTarget Color color);
}
