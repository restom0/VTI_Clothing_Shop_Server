package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.ProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ProductUpdateRequest;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface ProductService {
	List<Product> getAllProducts();

	void addProduct(ProductCreateRequest productCreateRequest) throws WrapperException;

	void deleteProduct(Long id) throws WrapperException;

	void updateProduct(ProductUpdateRequest productUpdateRequest, Long productId) throws WrapperException;

	Product getProductById(Long id) throws WrapperException;
}
