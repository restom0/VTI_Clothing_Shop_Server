package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import vn.vti.clothing_shop.dtos.ins.CategoryCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.entities.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	/** Maps to DTO. */
	CategoryDTO entityToDTO(Category category);

	/** Creates request to entity. */
	Category createRequestToEntity(CategoryCreateRequest categoryCreateRequest);

	/** Updates request to entity. */
	Category updateRequestToEntity(CategoryUpdateRequest categoryUpdateRequest, @MappingTarget Category category);
}
