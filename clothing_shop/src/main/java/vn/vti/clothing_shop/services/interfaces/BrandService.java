package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface BrandService {
	List<Brand> getBrands();

	Brand createBrand(BrandCreateRequest brandCreateRequest) throws WrapperException;

	Brand updateBrand(BrandUpdateRequest brandUpdateRequest, Long id) throws WrapperException;

	void deleteBrand(Long id) throws WrapperException;

	Brand findBrandById(Long id) throws WrapperException;

	Long countBrand();
}
