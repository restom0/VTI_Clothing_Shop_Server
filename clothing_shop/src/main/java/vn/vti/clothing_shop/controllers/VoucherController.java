package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.dtos.ins.VoucherCreateRequest;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateRequest;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.VoucherService;

@AllArgsConstructor
@RestController
@RequestMapping("/voucher")
public class VoucherController {
    private final VoucherService voucherService;

    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getAllVouchers() {
        return ResponseHandler.successBuilder(HttpStatus.OK, voucherService.getAllVouchers());

    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> getVoucherById(@PathVariable @NotNull(message = "Vui lòng chọn voucher") Long id) {
        try {
            return ResponseHandler.successBuilder(HttpStatus.OK, voucherService.findVoucherById(id));
        } catch (WrapperException ex) {
            return ResponseHandler.exceptionBuilder(ex);
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<BaseMessageResponse> getVoucherByCode(@PathVariable @Valid @NotNull(message = "Vui lòng nhập mã voucher") String code) {
        try {
            return ResponseHandler.successBuilder(HttpStatus.OK, "Lấy voucher thành công", voucherService.findVoucherByCode(code));
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/available")
    public ResponseEntity<BaseMessageResponse> getAllAvailableVouchers() {
        return ResponseHandler.successBuilder(HttpStatus.OK, voucherService.getAllAvailableVouchers());
    }

    @PostMapping("/")
    public ResponseEntity<BaseMessageResponse> createVoucher(@RequestBody @NotNull(message = "Vui lòng nhập thông tin voucher") VoucherCreateRequest voucherCreateRequest) {
        try {
            voucherService.createVoucher(voucherCreateRequest);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Tạo voucher thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> updateVoucher(@PathVariable @NotNull(message = "Vui lòng chọn voucher") Long id, @RequestBody @NotNull(message = "Vui lòng nhập thông tin voucher") VoucherUpdateRequest voucherUpdateRequest) {
        try {
            voucherService.updateVoucher(voucherUpdateRequest, id);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Cập nhật voucher thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> deleteVoucher(@PathVariable @NotNull(message = "Vui lòng chọn voucher") Long id) {
        try {
            voucherService.deleteVoucher(id);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Xóa voucher thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
