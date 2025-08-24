package vn.vti.clothing_shop.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.OnSaleProductService;

@RestController
@RequestMapping("/on-sale-product")
@RequiredArgsConstructor
public class OnSaleProductController {
    private final OnSaleProductService onSaleProductService;


    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getAllOnSaleProducts() {
        return ResponseHandler.successBuilder(HttpStatus.OK, onSaleProductService.getAllOnSaleProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> getOnSaleProductById(@PathVariable Long id) {
        return ResponseHandler.successBuilder(HttpStatus.OK, onSaleProductService.getOnSaleProductById(id));
    }
}
