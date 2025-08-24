package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.constants.Filter;
import vn.vti.clothing_shop.dtos.ins.ImportedProductCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ImportedProductUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ColorDTO;
import vn.vti.clothing_shop.dtos.outs.ImportedProductDTO;
import vn.vti.clothing_shop.dtos.outs.MaterialDTO;
import vn.vti.clothing_shop.dtos.outs.SizeDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface ImportedProductService {
    List<ImportedProductDTO> getAllImportedProducts();

    void addImportedProduct(ImportedProductCreateRequest importedProductCreateRequest) throws WrapperException;

    void deleteImportedProduct(Long id) throws WrapperException;

    void updateImportedProduct(Long id, ImportedProductUpdateRequest importedProductUpdateRequest) throws WrapperException;

    ImportedProductDTO findImportedProductById(Long id) throws WrapperException;

    List<ImportedProductDTO> getImportedProductByFilter(Filter filter, Long id) throws WrapperException;

    List<ColorDTO> getColors();

    List<MaterialDTO> getMaterials();

    List<SizeDTO> getSizes();
}
