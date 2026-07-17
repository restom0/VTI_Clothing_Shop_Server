package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.CategoryCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CategoryUpdateRequest;
import vn.vti.clothing_shop.entities.Category;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface CategoryService {
	/** Gets all categories. */
	List<Category> getAllCategories();

	/** Adds category. */
	Category addCategory(CategoryCreateRequest categoryCreateRequest) throws WrapperException;

	/** Updates category. */
	Category updateCategory(CategoryUpdateRequest categoryUpdateRequest, Long id) throws WrapperException;

	/** Deletes category. */
	void deleteCategory(Long id) throws WrapperException;

	/** Gets category by id. */
	Category getCategoryById(Long id) throws WrapperException;

	/** Counts category. */
	Long countCategory();
}
