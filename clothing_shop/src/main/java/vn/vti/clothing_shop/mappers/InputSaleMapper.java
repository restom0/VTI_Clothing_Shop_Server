package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.InputSaleCreateDTO;
import vn.vti.clothing_shop.dtos.ins.InputSaleUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.InputSaleDTO;
import vn.vti.clothing_shop.dtos.outs.OnSaleProductDTO;
import vn.vti.clothing_shop.entities.InputSale;
import vn.vti.clothing_shop.entities.OnSaleProduct;
import vn.vti.clothing_shop.requests.InputSaleCreateRequest;
import vn.vti.clothing_shop.requests.InputSaleUpdateRequest;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InputSaleMapper {
    private final ModelMapper modelMapper;
    private final OnSaleProductMapper onSaleProductMapper;
    @Autowired
    public InputSaleMapper(ModelMapper modelMapper, OnSaleProductMapper onSaleProductMapper) {
        this.modelMapper = modelMapper;
        this.onSaleProductMapper = onSaleProductMapper;
    }

    public InputSaleDTO EntityToDTO(InputSale inputSale, List<OnSaleProduct> onSaleProducts) {
        InputSaleDTO inputSaleDTO = modelMapper.map(inputSale, InputSaleDTO.class);
        List<OnSaleProductDTO> onSaleProductDTOs = onSaleProductMapper.EntityToDTO(onSaleProducts);
        inputSaleDTO.setOnSaleProducts(onSaleProductDTOs);
        return inputSaleDTO;
    }

    public List<InputSaleDTO> EntityToDTO(List<InputSale> inputSales, List<List<OnSaleProduct>> onSaleProducts){
        return inputSales.stream()
                .map(inputSale -> EntityToDTO(inputSale, onSaleProducts.get(inputSales.indexOf(inputSale))))
                .collect(Collectors.toList());
    }

    public InputSaleCreateDTO CreateRequestToCreateDTO(InputSaleCreateRequest inputSaleCreateRequest){
        return modelMapper.map(inputSaleCreateRequest, InputSaleCreateDTO.class);
    }

    public InputSale CreateDTOToEntity(InputSaleCreateDTO inputSaleCreateDTO){
        return modelMapper.map(inputSaleCreateDTO, InputSale.class);
    }

    public InputSaleUpdateDTO UpdateRequestToUpdateDTO(InputSaleUpdateRequest inputSaleUpdateRequest, Long id){
        InputSaleUpdateDTO inputSaleUpdateDTO = modelMapper.map(inputSaleUpdateRequest, InputSaleUpdateDTO.class);
        inputSaleUpdateDTO.setId(id);
        return inputSaleUpdateDTO;
    }

    public InputSale UpdateDTOToEntity(InputSaleUpdateDTO inputSaleUpdateDTO, InputSale inputSale){
        inputSale.setSalePercentage(inputSaleUpdateDTO.getSalePercentage());
        inputSale.setDiscount(inputSaleUpdateDTO.getDiscount());
        inputSale.setAvailable_date(inputSaleUpdateDTO.getAvailable_date());
        inputSale.setEnd_date(inputSaleUpdateDTO.getEnd_date());
        return inputSale;
    }
}
