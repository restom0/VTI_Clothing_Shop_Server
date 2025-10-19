package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.SizeDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Size;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SizeMapper {

    SizeDTO entityToDTO(Size size);

    List<SizeDTO> listEntityToDTO(List<Size> sizes);

    Size createRequestToEntity(ImportedProductCreateRequest importedProductCreateRequest, Category category);

    Size updateRequestToEntity(ImportedProductUpdateRequest importedProductUpdateRequest, Category category, @MappingTarget Size size);
}
