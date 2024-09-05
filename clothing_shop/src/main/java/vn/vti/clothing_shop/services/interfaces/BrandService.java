package vn.vti.clothing_shop.services.interfaces;

import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.BrandCreateDTO;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.responses.BrandResponse;

import java.util.List;

@Component
public interface BrandService {
    List<BrandDTO> getAllBrands();
    Boolean addBrand(BrandCreateDTO brandCreateDTO);
    Boolean updateBrand(BrandUpdateDTO brandUpdateDTO);
    Boolean deleteBrand(Long id);
    BrandResponse getBrandById(Long id);
    Long countBrand();
}
