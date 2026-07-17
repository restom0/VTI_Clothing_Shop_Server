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
	/** Gets all imported products. */
	List<ImportedProduct> getAllImportedProducts();

	/** Adds imported product. */
	void addImportedProduct(ImportedProductCreateRequest importedProductCreateRequest) throws WrapperException;

	/** Deletes imported product. */
	void deleteImportedProduct(Long id) throws WrapperException;

	/** Updates imported product. */
	void updateImportedProduct(Long id, ImportedProductUpdateRequest importedProductUpdateRequest) throws WrapperException;

	/** Finds imported product by id. */
	ImportedProduct findImportedProductById(Long id) throws WrapperException;

	/** Gets imported product by filter. */
	List<ImportedProduct> getImportedProductByFilter(Filter filter, Long id) throws WrapperException;

	/** Gets colors. */
	List<Color> getColors();

	/** Gets materials. */
	List<Material> getMaterials();

	/** Gets sizes. */
	List<Size> getSizes();
}
