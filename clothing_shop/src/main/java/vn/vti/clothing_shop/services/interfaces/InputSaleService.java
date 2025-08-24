package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.InputSaleCreateRequest;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.InputSaleDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface InputSaleService {
    List<InputSaleDTO> getAllInputSale();

    InputSaleDTO getInputSaleById(Long id) throws WrapperException;

    void createInputSale(InputSaleCreateRequest inputSaleCreateRequest);

    void updateInputSale(InputSaleUpdateRequest inputSaleUpdateRequest, Long inputSaleId) throws WrapperException;

    void deleteInputSale(Long id);
}
