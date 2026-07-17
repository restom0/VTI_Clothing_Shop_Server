package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.entities.OnSaleProduct;

import java.util.List;

public interface OnSaleProductService {
	/** Gets all on sale products. */
	List<OnSaleProduct> getAllOnSaleProducts();

	/** Gets on sale product by id. */
	List<OnSaleProduct> getOnSaleProductById(Long id);
}
