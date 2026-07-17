package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.entities.Brand;

@Mapper(componentModel = "spring")
public interface BrandMapper {
	/** Maps to DTO. */
	BrandDTO entityToDTO(Brand brand);

	/** Creates request to entity. */
	Brand createRequestToEntity(BrandCreateRequest brandCreateRequest);

	/** Updates request to entity. */
	Brand updateRequestToEntity(BrandUpdateRequest brandUpdateRequest, @MappingTarget Brand brand);

}
