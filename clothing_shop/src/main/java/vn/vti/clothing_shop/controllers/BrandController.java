package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.vti.clothing_shop.mappers.BrandMapper;
import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.implementations.brandService;
import vn.vti.clothing_shop.services.interfaces.BrandService;
import vn.vti.clothing_shop.utils.ParameterUtils;

@RequestMapping("/brand")
@RestController
public class BrandController {
    private final BrandService brandService;
    private final BrandMapper brandMapper;

    @Autowired
    public BrandController(BrandService brandService, BrandMapper brandMapper) {
        this.brandService = brandService;
        this.brandMapper = brandMapper;
    }

    @GetMapping("/")
    public ResponseEntity<Object> getAllBrands() {
        try {
            return ResponseHandler.responseBuilder(200, "Lấy danh sách thương hiệu thành công", brandService.getAllBrands(), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }

    }

    @PostMapping("/")
    public ResponseEntity<?> addBrand(@RequestBody @Valid BrandCreateRequest brandCreateRequest) {
        try {
            if (Boolean.TRUE.equals(brandService.addBrand(brandMapper.BrandCreateRequestToBrandCreateDTO(brandCreateRequest)))) {
                return ResponseHandler.responseBuilder(201, "Thêm thương hiệu thành công", null, HttpStatus.CREATED);
            }
            throw new InternalServerErrorException("Thêm thương hiệu thất bại");
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@RequestBody @Valid BrandUpdateRequest brandUpdateRequest, @PathVariable @NotNull(message = "Vui lòng chọn brand") Long id, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                return ParameterUtils.showBindingResult(bindingResult);
            }
            if (brandService.updateBrand(brandMapper.BrandUpdateRequestToBrandUpdateDTO(brandUpdateRequest, id))) {
                return ResponseHandler.responseBuilder(200, "Cập nhật thương hiệu thành công", null, HttpStatus.OK);
            }
            throw new InternalServerErrorException("Cập nhật thương hiệu thất bại");
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable @NotNull(message = "Vui lòng chọn brand") Long id) {
        try {
            if (brandService.deleteBrand(id)) {
                return ResponseHandler.responseBuilder(200, "Xóa thương hiệu thành công", null, HttpStatus.OK);
            }
            throw new InternalServerErrorException("Xóa thương hiệu thất bại");
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable @NotNull(message = "Vui lòng chọn brand") Long id) {
        try {
            return ResponseHandler.responseBuilder(200, "Lấy thông tin thương hiệu thành công", brandService.getBrandById(id), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
