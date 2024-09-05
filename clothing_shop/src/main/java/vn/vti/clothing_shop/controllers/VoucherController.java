package vn.vti.clothing_shop.controllers;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.vti.clothing_shop.exceptions.InternalServerErrorException;
import vn.vti.clothing_shop.mappers.VoucherMapper;
import vn.vti.clothing_shop.requests.VoucherCreateRequest;
import vn.vti.clothing_shop.requests.VoucherUpdateRequest;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.implementations.VoucherServiceImplementation;
import vn.vti.clothing_shop.utils.ParameterUtils;

@RestController
@RequestMapping("/voucher")
public class VoucherController {

    private final VoucherServiceImplementation voucherServiceImplementation;
    private final VoucherMapper voucherMapper;

    @Autowired
    public VoucherController(VoucherServiceImplementation voucherServiceImplementation, VoucherMapper voucherMapper) {
        this.voucherServiceImplementation = voucherServiceImplementation;
        this.voucherMapper = voucherMapper;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllVouchers() {
        try {
            return ResponseHandler.responseBuilder(200, "Lấy danh sách voucher thành công", voucherServiceImplementation.getAllVouchers(), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucherById(@PathVariable @NotNull(message = "Vui lòng chọn voucher") Long id) {
        try {
            return ResponseHandler.responseBuilder(200, "Lấy voucher thành công", voucherServiceImplementation.getVoucherById(id), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getVoucherByCode(@PathVariable @NotNull(message = "Vui lòng nhập mã voucher") String code) {
        try {
            return ResponseHandler.responseBuilder(200, "Lấy voucher thành công", voucherServiceImplementation.getVoucherByCode(code), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAllAvailableVouchers() {
        try {
            return ResponseHandler.responseBuilder(200, "Lấy danh sách voucher có sẵn thành công", voucherServiceImplementation.getAllAvailableVouchers(), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createVoucher(@RequestBody @NotNull(message = "Vui lòng nhập thông tin voucher") VoucherCreateRequest voucherCreateRequest,BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            if (voucherServiceImplementation.createVoucher(voucherMapper.createRequestToCreateDTO(voucherCreateRequest)))
                return ResponseHandler.responseBuilder(200, "Tạo voucher thành công", null, HttpStatus.OK);
            throw new InternalServerErrorException("Tạo voucher thất bại");
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVoucher(@PathVariable @NotNull(message = "Vui lòng chọn voucher") Long id, @RequestBody @NotNull(message = "Vui lòng nhập thông tin voucher") VoucherUpdateRequest voucherUpdateRequest, BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            if(voucherServiceImplementation.updateVoucher(voucherMapper.updateRequestToUpdateDTO(voucherUpdateRequest, id)))
                return ResponseHandler.responseBuilder(200, "Cập nhật voucher thành công", null, HttpStatus.OK);
            throw new InternalServerErrorException("Tạo voucher thất bại");
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVoucher(@PathVariable @NotNull(message = "Vui lòng chọn voucher") Long id) {
        try {
            if (voucherServiceImplementation.deleteVoucher(id))
                return ResponseHandler.responseBuilder(200, "Xóa voucher thành công", null, HttpStatus.OK);
            throw new InternalServerErrorException("Xóa voucher thất bại");
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
