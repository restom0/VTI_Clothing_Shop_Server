package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.VoucherCreateRequest;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.VoucherDTO;
import vn.vti.clothing_shop.entities.Voucher;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface VoucherMapper {

    VoucherDTO entityToDTO(Voucher voucher);

    List<VoucherDTO> listEntityToDTO(List<Voucher> vouchers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stock", source = "voucherCreateRequest.inputStock")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Voucher createRequestToEntity(VoucherCreateRequest voucherCreateRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stock", source = "voucherUpdateRequest.inputStock")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "version", source = "voucherUpdateRequest.version")
    Voucher updateRequestToEntity(VoucherUpdateRequest voucherUpdateRequest, @MappingTarget Voucher voucher);
}
