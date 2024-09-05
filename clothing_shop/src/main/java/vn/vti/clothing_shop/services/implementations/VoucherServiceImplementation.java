package vn.vti.clothing_shop.services.implementations;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.VoucherCreateDTO;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.VoucherDTO;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.mappers.VoucherMapper;
import vn.vti.clothing_shop.repositories.VoucherRepository;
import vn.vti.clothing_shop.services.interfaces.VoucherService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoucherServiceImplementation implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    @Autowired
    public VoucherServiceImplementation(VoucherRepository voucherRepository, VoucherMapper voucherMapper) {
        this.voucherRepository = voucherRepository;
        this.voucherMapper = voucherMapper;
    }

    //@Cacheable(value = "vouchers")
    public List<VoucherDTO> getAllVouchers(){
        return voucherMapper.EntityToDTO(voucherRepository.findAll());
    };

    //@Cacheable(value = "vouchers", key = "#available")
    public List<VoucherDTO> getAllAvailableVouchers(){
        return voucherMapper.EntityToDTO(voucherRepository.findAllWithPositiveStockAndAvailable());
    };

    //@Cacheable(value = "vouchers", key = "#id")
    public VoucherDTO getVoucherById(Long id){
        return voucherMapper.EntityToDTO(voucherRepository.findById(id).orElseThrow(()->new NotFoundException("Voucher not found")));
    };

    //@Cacheable(value = "vouchers", key = "#code")
    public VoucherDTO getVoucherByCode(String code){
        return voucherMapper.EntityToDTO(voucherRepository.findByCode(code).orElseThrow(()->new NotFoundException("Voucher not found")));
    };

    //@CacheEvict(value = "vouchers", allEntries = true)
    @Transactional
    public Boolean createVoucher(VoucherCreateDTO voucherCreateDTO){
        if(voucherRepository.findByCode(voucherCreateDTO.getCode()).isPresent()){
            throw new BadRequestException("Voucher code already exists");
        }
        voucherRepository.save(voucherMapper.createDTOToEntity(voucherCreateDTO));
        return true;
    };

    //@CachePut(value = "vouchers")
    @Transactional
    public Boolean updateVoucher(VoucherUpdateDTO voucherUpdateDTO){
        Voucher voucher = voucherRepository.findById(voucherUpdateDTO.getId()).orElseThrow(()->new NotFoundException("Voucher not found"));
        voucherRepository.save(voucherMapper.updateDTOToEntity(voucher,voucherUpdateDTO));
        return true;
    };

    //@CacheEvict(value = "vouchers", allEntries = true)
    @Transactional
    public Boolean deleteVoucher(Long id){
        Voucher voucher = voucherRepository.findById(id).orElseThrow(()->new NotFoundException("Voucher not found"));
        voucher.setDeleted_at(LocalDateTime.now());
        voucherRepository.save(voucher);
        return true;
    };

}
