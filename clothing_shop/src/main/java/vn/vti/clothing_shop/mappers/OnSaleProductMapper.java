package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import vn.vti.clothing_shop.dtos.outs.OnSaleProductDTO;
import vn.vti.clothing_shop.entities.ImportedProduct;
import vn.vti.clothing_shop.entities.InputSale;
import vn.vti.clothing_shop.entities.OnSaleProduct;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OnSaleProductMapper {

    OnSaleProductDTO entityToDTO(OnSaleProduct onSaleProduct);

    List<OnSaleProductDTO> entityToDTO(List<OnSaleProduct> onSaleProducts);

    OnSaleProduct importProductAndInputSaleToOnSaleProduct(ImportedProduct importedProduct, InputSale inputSale);
}
