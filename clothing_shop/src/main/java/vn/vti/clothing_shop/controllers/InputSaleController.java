package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.vti.clothing_shop.exceptions.InternalServerErrorException;
import vn.vti.clothing_shop.mappers.InputSaleMapper;
import vn.vti.clothing_shop.requests.InputSaleCreateRequest;
import vn.vti.clothing_shop.requests.InputSaleUpdateRequest;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.implementations.InputSaleServiceImplementation;
import vn.vti.clothing_shop.services.implementations.OnSaleProductServiceImplementation;
import vn.vti.clothing_shop.utils.ParameterUtils;

@RestController
@RequestMapping("/input-sale")
public class InputSaleController {
    private final InputSaleServiceImplementation inputSaleServiceImplementation;
    private final InputSaleMapper inputSaleMapper;

    @Autowired
    public InputSaleController(InputSaleServiceImplementation inputSaleServiceImplementation, OnSaleProductServiceImplementation onSaleProductServiceImplementation, InputSaleMapper inputSaleMapper) {
        this.inputSaleServiceImplementation = inputSaleServiceImplementation;
        this.inputSaleMapper = inputSaleMapper;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllInputSale(){
        try{
            return ResponseHandler.responseBuilder(200,"Lấy danh sách sản phẩm nhập thành công",inputSaleServiceImplementation.getAllInputSale(), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInputSaleById(@PathVariable @NotNull(message = "Vui lòng nhập id") Long id){
        try{
            return ResponseHandler.responseBuilder(200,"Lấy sản phẩm nhập thành công",inputSaleServiceImplementation.getInputSaleById(id), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createInputSale(@RequestBody @Valid @NotNull(message = "Vui lòng nhập thông tin sản phẩm nhập") InputSaleCreateRequest inputSaleCreateRequest, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()) {
                return ParameterUtils.showBindingResult(bindingResult);
            }
            if(inputSaleServiceImplementation.createInputSale(inputSaleMapper.CreateRequestToCreateDTO(inputSaleCreateRequest))){
                return ResponseHandler.responseBuilder(201,"Thêm sản phẩm nhập thành công",null, HttpStatus.CREATED);
            }
            throw new InternalServerErrorException("Thêm sản phẩm nhập thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateInputSale(@PathVariable @NotNull(message = "Vui lòng nhập id") Long id, @RequestBody @Valid @NotNull(message = "Vui lòng nhập thông tin sản phẩm nhập") InputSaleUpdateRequest inputSaleUpdateRequest, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()) {
                return ParameterUtils.showBindingResult(bindingResult);
            }
            if(inputSaleServiceImplementation.updateInputSale(inputSaleMapper.UpdateRequestToUpdateDTO(inputSaleUpdateRequest, id))){
                return ResponseHandler.responseBuilder(200,"Cập nhật sản phẩm nhập thành công",null, HttpStatus.OK);
            }
            throw new InternalServerErrorException("Cập nhật sản phẩm nhập thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInputSale(@PathVariable @NotNull(message = "Vui lòng nhập id") Long id){
        try{
            if(inputSaleServiceImplementation.deleteInputSale(id)){
                return ResponseHandler.responseBuilder(200,"Xóa sản phẩm nhập thành công",null, HttpStatus.OK);
            }
            throw new InternalServerErrorException("Xóa sản phẩm nhập thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
