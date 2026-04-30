package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.entities.OnSaleProduct;

import java.util.List;

public interface OnSaleProductService {
	List<OnSaleProduct> getAllOnSaleProducts();

	List<OnSaleProduct> getOnSaleProductById(Long id);
}
