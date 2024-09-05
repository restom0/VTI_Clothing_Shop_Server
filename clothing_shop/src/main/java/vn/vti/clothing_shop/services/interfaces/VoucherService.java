package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.VoucherCreateDTO;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.VoucherDTO;

import java.util.List;

public interface VoucherService {
    List<VoucherDTO> getAllVouchers();
    List<VoucherDTO> getAllAvailableVouchers();
    VoucherDTO getVoucherById(Long id);
    VoucherDTO getVoucherByCode(String code);
    Boolean createVoucher(VoucherCreateDTO voucherCreateDTO);
    Boolean updateVoucher(VoucherUpdateDTO voucherUpdateDTO);
    Boolean deleteVoucher(Long id);
}
