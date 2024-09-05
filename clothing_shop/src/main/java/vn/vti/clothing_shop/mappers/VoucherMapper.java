package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.VoucherCreateDTO;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.VoucherDTO;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.requests.VoucherCreateRequest;
import vn.vti.clothing_shop.requests.VoucherUpdateRequest;

import java.util.List;

@Component
public class VoucherMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public VoucherMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public VoucherDTO EntityToDTO(Voucher voucher) {
        return modelMapper.map(voucher, VoucherDTO.class);
    }

    public List<VoucherDTO> EntityToDTO(List<Voucher> vouchers) {
        return vouchers.stream().map(this::EntityToDTO).toList();
    }

    public VoucherCreateDTO createRequestToCreateDTO(VoucherCreateRequest voucherCreateRequest) {
        return modelMapper.map(voucherCreateRequest, VoucherCreateDTO.class);
    }

    public Voucher createDTOToEntity(VoucherCreateDTO voucherCreateDTO) {
        Voucher voucher = modelMapper.map(voucherCreateDTO, Voucher.class);
        voucher.setStock(voucherCreateDTO.getInput_stock());
        return voucher;
    }

    public Voucher updateDTOToEntity(Voucher voucher, VoucherUpdateDTO voucherUpdateDTO) {
        voucher.setStock(voucherUpdateDTO.getInput_stock());
        voucher.setAvailable_date(voucherUpdateDTO.getAvailable_date());
        voucher.setExpired_date(voucherUpdateDTO.getExpired_date());
        voucher.setValue(voucherUpdateDTO.getValue());
        voucher.setCode(voucherUpdateDTO.getCode());
        return voucher;
    }

    public VoucherUpdateDTO updateRequestToUpdateDTO(VoucherUpdateRequest voucherUpdateRequest, Long id) {
        VoucherUpdateDTO voucherUpdateDTO = modelMapper.map(voucherUpdateRequest, VoucherUpdateDTO.class);
        voucherUpdateDTO.setId(id);
        return voucherUpdateDTO;
    }

}
