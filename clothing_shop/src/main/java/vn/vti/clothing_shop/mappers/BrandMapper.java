package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.BrandCreateDTO;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.requests.BrandCreateRequest;
import vn.vti.clothing_shop.requests.BrandUpdateRequest;

@Component
public class BrandMapper {

    private final ModelMapper mapper;

    @Autowired
    public BrandMapper(ModelMapper mapper) {
        this.mapper = mapper;
    }

    public BrandDTO EntityToDTO(Brand brand) {
        return mapper.map(brand, BrandDTO.class);
    }

    public BrandCreateDTO BrandCreateRequestToBrandCreateDTO(BrandCreateRequest brandCreateRequest){
        return mapper.map(brandCreateRequest, BrandCreateDTO.class);
    }

    public BrandUpdateDTO BrandUpdateRequestToBrandUpdateDTO(BrandUpdateRequest brandUpdateRequest,Long id){
        BrandUpdateDTO brandDTO = mapper.map(brandUpdateRequest, BrandUpdateDTO.class);
        brandDTO.setId(id);
        return brandDTO;
    }

    public Brand BrandCreateDTOToEntity(BrandCreateDTO brandCreateDTO) {
        return mapper.map(brandCreateDTO, Brand.class);
    }
    public Brand BrandUpdateDTOToEntity(BrandUpdateDTO brandUpdateDTO,Brand brand) {
        brand.setName(brandUpdateDTO.getName());
        brand.setDescription(brandUpdateDTO.getDescription());
        return brand;
    }
}