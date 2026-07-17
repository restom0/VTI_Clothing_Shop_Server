package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.ProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateRequest;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface ProductService {
	/** Gets all products. */
	List<Product> getAllProducts();

	/** Adds product. */
	void addProduct(ProductCreateRequest productCreateRequest) throws WrapperException;

	/** Deletes product. */
	void deleteProduct(Long id) throws WrapperException;

	/** Updates product. */
	void updateProduct(ProductUpdateRequest productUpdateRequest, Long productId) throws WrapperException;

	/** Gets product by id. */
	Product getProductById(Long id) throws WrapperException;
}
