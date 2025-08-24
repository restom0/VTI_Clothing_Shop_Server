package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.InputSaleDTO;
import vn.vti.clothing_shop.entities.InputSale;

@Mapper(componentModel = "spring",
        uses = {OnSaleProductMapper.class})
public interface InputSaleMapper {
    InputSaleDTO entityToDTO(InputSale inputSale);

    InputSale createRequestEntity(InputSaleCreateRequest inputSaleCreateRequest);

    InputSale updateRequestToEntity(InputSaleUpdateRequest inputSaleUpdateRequest, @MappingTarget InputSale inputSale);
}

