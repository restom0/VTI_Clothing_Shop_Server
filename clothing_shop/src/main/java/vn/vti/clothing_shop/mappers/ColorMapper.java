package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ColorDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.entities.Color;

import java.util.List;

@Mapper
public interface ColorMapper {
    ColorDTO entityToDTO(Color color);

    List<ColorDTO> entityToDTO(List<Color> colors);

    Color createRequestToEntity(ImportedProductCreateRequest importedProductCreateRequest, Category category);

    Color updateRequestToEntity(ImportedProductUpdateRequest importedProductUpdateRequest, Category category, @MappingTarget Color color);
}
