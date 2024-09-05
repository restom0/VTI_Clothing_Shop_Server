package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.OnSaleProductCreateDTO;
import vn.vti.clothing_shop.dtos.ins.OnSaleProductUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.OnSaleProductDTO;
import vn.vti.clothing_shop.entities.ImportedProduct;
import vn.vti.clothing_shop.entities.InputSale;
import vn.vti.clothing_shop.entities.OnSaleProduct;

import java.util.List;

@Component
public class OnSaleProductMapper {

    private final ModelMapper modelMapper;
    private final ImportedProductMapper importedProductMapper;

    @Autowired
    public OnSaleProductMapper(ModelMapper modelMapper, ImportedProductMapper importedProductMapper) {
        this.modelMapper = modelMapper;
        this.importedProductMapper = importedProductMapper;
    }

    public OnSaleProductDTO EntityToDTO(OnSaleProduct onSaleProduct) {
        OnSaleProductDTO onSaleProductDTO = modelMapper.map(onSaleProduct, OnSaleProductDTO.class);
        onSaleProductDTO.setProduct_id(importedProductMapper.EntityToDTO(onSaleProduct.getProduct_id()));
        return onSaleProductDTO;
    }

    public List<OnSaleProductDTO> EntityToDTO(List<OnSaleProduct> onSaleProducts) {
        return onSaleProducts.stream()
                .map(this::EntityToDTO)
                .toList();
    }

    public OnSaleProductCreateDTO CreateDTOToEntity(OnSaleProductCreateDTO onSaleProductCreateDTO) {
        return modelMapper.map(onSaleProductCreateDTO, OnSaleProductCreateDTO.class);
    }

    public OnSaleProduct UpdateDTOToEntity(OnSaleProductUpdateDTO onSaleProductUpdateDTO, OnSaleProduct onSaleProduct) {
        return modelMapper.map(onSaleProductUpdateDTO, OnSaleProduct.class);
    }

    public OnSaleProduct ImportProductToOnSaleProduct(ImportedProduct importedProduct, InputSale inputSale) {
        OnSaleProduct onSaleProduct = new OnSaleProduct();
        onSaleProduct.setProduct_id(importedProduct);
        onSaleProduct.setSale_price(importedProduct.getImportPrice() * inputSale.getSalePercentage()/100);
        onSaleProduct.setDiscount(inputSale.getDiscount());
        onSaleProduct.setInput_sale_id(inputSale);
        return onSaleProduct;
    }

//    public List<OnSaleProductDTO> ImportProductToOnSaleProduct(List<ImportedProduct> importedProducts, InputSale inputSale) {
//        return importedProducts.stream()
//                .map(importedProduct -> ImportProductToOnSaleProduct(importedProduct, inputSale))
//                .map(this::EntityToDTO)
//                .toList();
//    }

}
