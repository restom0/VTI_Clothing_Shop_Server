package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.VoucherCreateRequest;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.VoucherDTO;
import vn.vti.clothing_shop.entities.Voucher;

import java.util.List;

@Mapper
public interface VoucherMapper {

    VoucherDTO entityToDTO(Voucher voucher);

    List<VoucherDTO> listEntityToDTO(List<Voucher> vouchers);

    Voucher createRequestToEntity(VoucherCreateRequest voucherCreateRequest);

    Voucher updateRequestToEntity(VoucherUpdateRequest voucherUpdateRequest, @MappingTarget Voucher voucher);
}
