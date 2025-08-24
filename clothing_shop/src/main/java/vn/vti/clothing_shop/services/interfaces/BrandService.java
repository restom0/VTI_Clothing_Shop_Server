package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface BrandService {
    List<BrandDTO> getBrands();

    BrandDTO createBrand(BrandCreateRequest brandCreateRequest) throws WrapperException;

    BrandDTO updateBrand(BrandUpdateRequest brandUpdateRequest, Long id) throws WrapperException;

    void deleteBrand(Long id) throws WrapperException;

    BrandDTO findBrandById(Long id) throws WrapperException;

    Long countBrand();
}
