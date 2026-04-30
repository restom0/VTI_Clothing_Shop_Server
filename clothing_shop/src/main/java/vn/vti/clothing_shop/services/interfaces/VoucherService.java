package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.VoucherCreateRequest;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateRequest;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface VoucherService {
	List<Voucher> getAllVouchers();

	List<Voucher> getAllAvailableVouchers();

	Voucher findVoucherById(Long id) throws WrapperException;

	Voucher findVoucherByCode(String code) throws WrapperException;

	void createVoucher(VoucherCreateRequest voucherCreateRequest) throws WrapperException;

	void updateVoucher(VoucherUpdateRequest voucherUpdateRequest, Long id) throws WrapperException;

	void deleteVoucher(Long id) throws WrapperException;
}
