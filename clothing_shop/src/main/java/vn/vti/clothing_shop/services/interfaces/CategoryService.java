package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.CategoryCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();

    CategoryDTO addCategory(CategoryCreateRequest categoryCreateRequest) throws WrapperException;

    CategoryDTO updateCategory(CategoryUpdateRequest categoryUpdateRequest, Long id) throws WrapperException;

    void deleteCategory(Long id) throws WrapperException;

    CategoryDTO getCategoryById(Long id) throws WrapperException;

    Long countCategory();
}
