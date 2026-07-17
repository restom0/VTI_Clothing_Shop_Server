package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.InputSaleDTO;
import vn.vti.clothing_shop.entities.InputSale;

@Mapper(componentModel = "spring",
        uses = { OnSaleProductMapper.class })
public interface InputSaleMapper {
	/** Maps to DTO. */
	@Mapping(target = "availableDate", source = "startDate")
	InputSaleDTO entityToDTO(InputSale inputSale);

	/** Creates request entity. */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "startDate", source = "availableDate")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	InputSale createRequestEntity(InputSaleCreateRequest inputSaleCreateRequest);

	/** Updates request to entity. */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "filter", ignore = true)
	@Mapping(target = "filterId", ignore = true)
	@Mapping(target = "startDate", source = "availableDate")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", source = "inputSaleUpdateRequest.version")
	InputSale updateRequestToEntity(InputSaleUpdateRequest inputSaleUpdateRequest, @MappingTarget InputSale inputSale);
}
