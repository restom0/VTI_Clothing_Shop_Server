package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.outs.OnSaleProductDTO;

import java.util.List;

public interface OnSaleProductService {
    List<OnSaleProductDTO> getAllOnSaleProducts();
    List<OnSaleProductDTO> getOnSaleProductById(Long id);
}
