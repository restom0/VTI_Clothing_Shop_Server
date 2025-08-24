package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.CategoryCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.entities.Category;

@Mapper
public interface CategoryMapper {
    CategoryDTO entityToDTO(Category category);

    Category createRequestToEntity(CategoryCreateRequest categoryCreateRequest);

    Category updateRequestToEntity(CategoryUpdateRequest categoryUpdateRequest, @MappingTarget Category category);
}
