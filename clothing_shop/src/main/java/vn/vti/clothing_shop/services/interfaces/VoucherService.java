package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.VoucherCreateRequest;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateRequest;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface VoucherService {
	/** Gets all vouchers. */
	List<Voucher> getAllVouchers();

	/** Gets all available vouchers. */
	List<Voucher> getAllAvailableVouchers();

	/** Finds voucher by id. */
	Voucher findVoucherById(Long id) throws WrapperException;

	/** Finds voucher by code. */
	Voucher findVoucherByCode(String code) throws WrapperException;

	/** Creates voucher. */
	void createVoucher(VoucherCreateRequest voucherCreateRequest) throws WrapperException;

	/** Updates voucher. */
	void updateVoucher(VoucherUpdateRequest voucherUpdateRequest, Long id) throws WrapperException;

	/** Deletes voucher. */
	void deleteVoucher(Long id) throws WrapperException;
}
