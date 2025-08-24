package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.MaterialDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Material;

@Mapper(componentModel = "spring")
public interface MaterialMapper {
    MaterialDTO entityToDTO(Material material);

    //    @Mapping(source = "importedProductCreateRequest.material", target = "name")
    Material createRequestToEntity(ImportedProductCreateRequest importedProductCreateRequest, Category category);

    //    @Mapping(source = "importedProductUpdateRequest.material", target = "name")
    Material updateRequestToEntity(ImportedProductUpdateRequest importedProductUpdateRequest, Category category, @MappingTarget Material material);
}
