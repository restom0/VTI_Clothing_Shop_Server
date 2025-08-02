package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import vn.vti.clothing_shop.dtos.ins.BrandCreateDTO;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;

@Mapper
public interface BrandMapper{
    BrandDTO entityToDTO(Brand brand);

    BrandCreateDTO brandCreateRequestToBrandCreateDTO(BrandCreateRequest brandCreateRequest);

    BrandUpdateDTO brandUpdateRequestToBrandUpdateDTO(BrandUpdateRequest brandUpdateRequest, Long id);

    Brand brandCreateDTOToEntity(BrandCreateDTO brandCreateDTO);

    Brand brandUpdateDTOToEntity(BrandUpdateDTO brandUpdateDTO, Brand brand);
}