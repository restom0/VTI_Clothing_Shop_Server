package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import vn.vti.clothing_shop.dtos.outs.OnSaleProductDTO;
import vn.vti.clothing_shop.entities.ImportedProduct;
import vn.vti.clothing_shop.entities.InputSale;
import vn.vti.clothing_shop.entities.OnSaleProduct;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OnSaleProductMapper {

	@Mapping(target = "discount", source = "inputSale.discount")
	OnSaleProductDTO entityToDTO(OnSaleProduct onSaleProduct);

	List<OnSaleProductDTO> entityToDTO(List<OnSaleProduct> onSaleProducts);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "product", source = "importedProduct")
	@Mapping(target = "inputSale", source = "inputSale")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	OnSaleProduct importProductAndInputSaleToOnSaleProduct(ImportedProduct importedProduct, InputSale inputSale);
}
