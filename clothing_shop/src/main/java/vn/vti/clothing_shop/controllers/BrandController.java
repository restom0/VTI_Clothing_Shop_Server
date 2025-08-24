package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.dtos.ins.BrandCreateRequest;
import vn.vti.clothing_shop.dtos.ins.BrandUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.BrandService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/brands")
public class BrandController {
    private final BrandService brandService;

    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getBrands() {
        final List<BrandDTO> brands = brandService.getBrands();
        return ResponseHandler.successBuilder(
                HttpStatus.OK,
                brands
        );
    }

    @PostMapping("/brand")
    public ResponseEntity<BaseMessageResponse> addBrand(@RequestBody @Valid BrandCreateRequest brandCreateRequest) {
        try {
            final BrandDTO brandDTO =
                    brandService.createBrand(brandCreateRequest);
            return ResponseHandler.successBuilder(
                    HttpStatus.CREATED,
                    brandDTO
            );
        } catch (WrapperException ex) {
            return ResponseHandler.exceptionBuilder(ex);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> updateBrand(@RequestBody @Valid BrandUpdateRequest brandUpdateRequest, @PathVariable @NotNull(message = "Vui lòng chọn brand") Long id) {
        try {
            final BrandDTO updatedBrand = brandService.updateBrand(brandUpdateRequest, id);
            return ResponseHandler.successBuilder(
                    HttpStatus.ACCEPTED,
                    updatedBrand
            );
        } catch (WrapperException ex) {
            return ResponseHandler.exceptionBuilder(ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> deleteBrand(@PathVariable @Valid @NotNull(message = "Vui lòng chọn brand") Long id) {
        try {
            brandService.deleteBrand(id);
            return ResponseHandler.successBuilder(
                    HttpStatus.OK,
                    "Xoá thương hiệu thành công"
            );
        } catch (WrapperException ex) {
            return ResponseHandler.exceptionBuilder(ex);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> getBrandById(@PathVariable @Valid @NotNull(message = "Vui lòng chọn brand") Long id) {
        try {
            final BrandDTO brand = brandService.findBrandById(id);
            return ResponseHandler.successBuilder(
                    HttpStatus.OK,
                    brand
            );
        } catch (WrapperException ex) {
            return ResponseHandler.exceptionBuilder(ex);
        }
    }
}
