package vn.vti.clothing_shop.services.interfaces;

import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.CategoryCreateDTO;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.responses.CategoryResponse;

import java.util.List;

@Component
public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    Boolean addCategory(CategoryCreateDTO categoryCreateDTO);
    Boolean updateCategory(CategoryUpdateDTO categoryUpdateDTO);
    Boolean deleteCategory(Long id);
    CategoryResponse getCategoryById(Long id);
    Long countCategory();
}
