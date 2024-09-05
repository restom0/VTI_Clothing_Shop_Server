package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.CategoryCreateDTO;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.requests.CategoryCreateRequest;
import vn.vti.clothing_shop.requests.CategoryUpdateRequest;

@Component
public class CategoryMapper {

    private final ModelMapper mapper;

    @Autowired
    public CategoryMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public CategoryDTO EntityToDTO(Category category) {
        return mapper.map(category, CategoryDTO.class);
    }

    public CategoryCreateDTO CategoryCreateRequestToCategoryCreateDTO(CategoryCreateRequest categoryCreateRequest){
        return mapper.map(categoryCreateRequest, CategoryCreateDTO.class);
    }

    public CategoryUpdateDTO CategoryUpdateRequestToCategoryUpdateDTO(CategoryUpdateRequest categoryUpdateRequest, Long id){
        CategoryUpdateDTO categoryDTO = mapper.map(categoryUpdateRequest, CategoryUpdateDTO.class);
        categoryDTO.setId(id);
        return categoryDTO;
    }

    public Category CategoryCreateDTOToEntity(CategoryCreateDTO categoryCreateDTO) {
        return mapper.map(categoryCreateDTO, Category.class);
    }
    public Category CategoryUpdateDTOToEntity(CategoryUpdateDTO categoryUpdateDTO,Category category) {
        category.setName(categoryUpdateDTO.getName());
        category.setDescription(categoryUpdateDTO.getDescription());
        return category;
    }
}