package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface BrandService {
	/** Gets brands. */
	List<Brand> getBrands();

	/** Creates brand. */
	Brand createBrand(BrandCreateRequest brandCreateRequest) throws WrapperException;

	/** Updates brand. */
	Brand updateBrand(BrandUpdateRequest brandUpdateRequest, Long id) throws WrapperException;

	/** Deletes brand. */
	void deleteBrand(Long id) throws WrapperException;

	/** Finds brand by id. */
	Brand findBrandById(Long id) throws WrapperException;

	/** Counts brand. */
	Long countBrand();
}
