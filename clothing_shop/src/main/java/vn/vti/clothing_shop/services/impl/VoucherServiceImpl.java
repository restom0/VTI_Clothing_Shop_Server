package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.dtos.ins.VoucherCreateRequest;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateRequest;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.exceptions.ConflictException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.VoucherMapper;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.repositories.VoucherRepository;
import vn.vti.clothing_shop.services.interfaces.VoucherService;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class VoucherServiceImpl implements VoucherService {

	private final VoucherRepository voucherRepository;
	private final VoucherMapper voucherMapper;
	private final MongoReadModelQueryService readModelQueryService;
	private final PostgresToMongoReadModelSyncService readModelSyncService;

	@Cacheable(value = "vouchers", key = "'all'")
	public List<Voucher> getAllVouchers() {
		List<Voucher> mongoVouchers = readModelQueryService.findAll(ReadModelType.VOUCHER, Voucher.class);
		if (mongoVouchers != null && !mongoVouchers.isEmpty()) {
			return mongoVouchers;
		}
		return voucherRepository.findByDeletedAtIsNullOrderByIdDesc();
	}

	@Cacheable(value = "vouchers", key = "'available'")
	public List<Voucher> getAllAvailableVouchers() {
		Long now = Instant.now().toEpochMilli();
		List<Voucher> mongoVouchers = readModelQueryService.findAll(ReadModelType.VOUCHER, Voucher.class);
		if (mongoVouchers != null && !mongoVouchers.isEmpty()) {
			return mongoVouchers.stream()
			                    .filter(voucher -> voucher.getStock() != null && voucher.getStock() > 0)
			                    .filter(voucher -> voucher.getAvailableDate() != null && voucher.getAvailableDate() <= now)
			                    .filter(voucher -> voucher.getEndDate() != null && voucher.getEndDate() >= now)
			                    .toList();
		}
		return voucherRepository.findByDeletedAtIsNullAndStockGreaterThanAndAvailableDateLessThanEqualAndEndDateGreaterThanEqual(
				0, now, now);
	}

	@Cacheable(value = "vouchers", key = "'id:' + #id")
	public Voucher findVoucherById(Long id) throws WrapperException {
		try {
			var mongoVoucher = readModelQueryService.findById(ReadModelType.VOUCHER, id, Voucher.class);
			if (mongoVoucher != null && mongoVoucher.isPresent()) {
				return mongoVoucher.get();
			}
			return voucherRepository.findByDeletedAtIsNullAndId(id)
			                        .orElseThrow(() -> new NotFoundException("messages.vouchers.notfound"));
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	@Cacheable(value = "vouchers", key = "'code:' + #code")
	public Voucher findVoucherByCode(String code) throws WrapperException {
		try {
			var mongoVoucher = readModelQueryService.findByLookupKey(ReadModelType.VOUCHER, code, Voucher.class);
			if (mongoVoucher != null && mongoVoucher.isPresent()) {
				return mongoVoucher.get();
			}
			return voucherRepository.findByDeletedAtIsNullAndCode(code)
			                        .orElseThrow(() -> new NotFoundException("messages.vouchers.notfound"));
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	@CacheEvict(value = "vouchers", allEntries = true)
	@Transactional
	public void createVoucher(VoucherCreateRequest voucherCreateRequest) throws WrapperException {
		try {
			if (voucherRepository.existsByDeletedAtIsNullAndCode(voucherCreateRequest.code())) {
				throw new ConflictException("messages.vouchers.exists");
			}
			Voucher voucher = voucherRepository.save(voucherMapper.createRequestToEntity(voucherCreateRequest));
			readModelSyncService.syncAfterCommit(ReadModelType.VOUCHER, voucher.getId());
		} catch (ConflictException ex) {
			throw new WrapperException(ex);
		}
	}

	@CacheEvict(value = "vouchers", allEntries = true)
	@Transactional
	public void updateVoucher(VoucherUpdateRequest voucherUpdateRequest, Long id) throws WrapperException {
		try {
			Voucher voucher = voucherRepository.findById(id).orElseThrow(
					() -> new NotFoundException("messages.vouchers.notfound"));
			Voucher savedVoucher = voucherRepository.save(voucherMapper.updateRequestToEntity(voucherUpdateRequest, voucher));
			readModelSyncService.syncAfterCommit(ReadModelType.VOUCHER, savedVoucher.getId());
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	@CacheEvict(value = "vouchers", allEntries = true)
	@Transactional
	public void deleteVoucher(Long id) throws WrapperException {
		try {
			Voucher voucher = voucherRepository.findById(id).orElseThrow(
					() -> new NotFoundException("messages.vouchers.notfound"));
			voucher.setDeletedAt(Instant.now().toEpochMilli());
			voucherRepository.save(voucher);
			readModelSyncService.removeAfterCommit(ReadModelType.VOUCHER, id);
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

}
