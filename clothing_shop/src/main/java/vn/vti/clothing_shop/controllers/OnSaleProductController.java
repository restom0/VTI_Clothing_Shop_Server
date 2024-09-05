package vn.vti.clothing_shop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.implementations.OnSaleProductServiceImplementation;

@RestController
@RequestMapping("/on-sale-product")
public class OnSaleProductController {
    @Autowired
    private final OnSaleProductServiceImplementation onSaleProductServiceImplementation;

    public OnSaleProductController(OnSaleProductServiceImplementation onSaleProductServiceImplementation) {
        this.onSaleProductServiceImplementation = onSaleProductServiceImplementation;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllOnSaleProducts() {
        try{
            return ResponseHandler.responseBuilder(200,"Lấy sản phẩm giảm giá thành công",onSaleProductServiceImplementation.getAllOnSaleProducts(), HttpStatus.OK);
        }
        catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOnSaleProductById(@PathVariable Long id) {
        try{
            return ResponseHandler.responseBuilder(200,"Lấy sản phẩm giảm giá thành công",onSaleProductServiceImplementation.getOnSaleProductById(id), HttpStatus.OK);
        }
        catch (Exception e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
