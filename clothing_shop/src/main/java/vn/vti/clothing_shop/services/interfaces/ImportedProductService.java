package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.constants.Filter;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.entities.Color;
import vn.vti.clothing_shop.entities.ImportedProduct;
import vn.vti.clothing_shop.entities.Material;
import vn.vti.clothing_shop.entities.Size;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface ImportedProductService {
	List<ImportedProduct> getAllImportedProducts();

	void addImportedProduct(ImportedProductCreateRequest importedProductCreateRequest) throws WrapperException;

	void deleteImportedProduct(Long id) throws WrapperException;

	void updateImportedProduct(Long id, ImportedProductUpdateRequest importedProductUpdateRequest) throws WrapperException;

	ImportedProduct findImportedProductById(Long id) throws WrapperException;

	List<ImportedProduct> getImportedProductByFilter(Filter filter, Long id) throws WrapperException;

	List<Color> getColors();

	List<Material> getMaterials();

	List<Size> getSizes();
}
