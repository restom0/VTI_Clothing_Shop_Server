package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.VoucherCreateRequest;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.VoucherDTO;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.exceptions.ConflictException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.VoucherMapper;
import vn.vti.clothing_shop.repositories.VoucherRepository;
import vn.vti.clothing_shop.services.interfaces.VoucherService;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    //@Cacheable(value = "vouchers")
    public List<VoucherDTO> getAllVouchers() {
        return voucherRepository.findByDeletedAtIsNullOrderByIdDesc()
                .stream()
                .map(voucherMapper::entityToDTO)
                .toList();
    }

    //@Cacheable(value = "vouchers", key = "#available")
    public List<VoucherDTO> getAllAvailableVouchers() {
        return voucherRepository.findByDeletedAtIsNullAndStockGreaterThanAndAvailableDateGreaterThanEqualAndEndDateLessThanEqual(0, Instant.now().toEpochMilli(), Instant.now().toEpochMilli())
                .stream()
                .map(voucherMapper::entityToDTO)
                .toList();
    }

    //@Cacheable(value = "vouchers", key = "#id")
    public VoucherDTO findVoucherById(Long id) throws WrapperException {
        try {
            final Voucher voucher = voucherRepository.findByDeletedAtIsNullAndId(id)
                    .orElseThrow(() -> new NotFoundException("Voucher not found"));
            return voucherMapper.entityToDTO(voucher);
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@Cacheable(value = "vouchers", key = "#code")
    public VoucherDTO findVoucherByCode(String code) throws WrapperException {
        try {
            final Voucher voucher = voucherRepository.findByDeletedAtIsNullAndCode(code)
                    .orElseThrow(() -> new NotFoundException("Voucher not found"));
            return voucherMapper.entityToDTO(voucher);
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CacheEvict(value = "vouchers", allEntries = true)
    @Transactional
    public void createVoucher(VoucherCreateRequest voucherCreateRequest) throws WrapperException {
        try {
            if (voucherRepository.existsByDeletedAtIsNullAndCode(voucherCreateRequest.code())) {
                throw new ConflictException("Voucher code already exists");
            }
            voucherRepository.save(voucherMapper.createRequestToEntity(voucherCreateRequest));
        } catch (ConflictException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CachePut(value = "vouchers")
    @Transactional
    public void updateVoucher(VoucherUpdateRequest voucherUpdateRequest, Long id) throws WrapperException {
        try {
            Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new NotFoundException("Voucher not found"));
            voucherRepository.save(voucherMapper.updateRequestToEntity(voucherUpdateRequest, voucher));
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CacheEvict(value = "vouchers", allEntries = true)
    @Transactional
    public void deleteVoucher(Long id) throws WrapperException {
        try {
            Voucher voucher = voucherRepository.findById(id).orElseThrow(() -> new NotFoundException("Voucher not found"));
            voucher.setDeletedAt(Instant.now().toEpochMilli());
            voucherRepository.save(voucher);
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

}
