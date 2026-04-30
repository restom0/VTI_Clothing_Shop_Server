package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.CategoryCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateRequest;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface CategoryService {
	List<Category> getAllCategories();

	Category addCategory(CategoryCreateRequest categoryCreateRequest) throws WrapperException;

	Category updateCategory(CategoryUpdateRequest categoryUpdateRequest, Long id) throws WrapperException;

	void deleteCategory(Long id) throws WrapperException;

	Category getCategoryById(Long id) throws WrapperException;

	Long countCategory();
}
