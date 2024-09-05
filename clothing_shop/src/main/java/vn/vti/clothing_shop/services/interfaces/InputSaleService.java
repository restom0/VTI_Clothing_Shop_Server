package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.InputSaleCreateDTO;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.InputSaleDTO;

import java.util.List;

public interface InputSaleService {
    List<InputSaleDTO> getAllInputSale();
    InputSaleDTO getInputSaleById(Long id);
    Boolean createInputSale(InputSaleCreateDTO inputSaleCreateDTO);
    Boolean updateInputSale(InputSaleUpdateDTO inputSaleUpdateDTO);
    Boolean deleteInputSale(Long id);
}
