package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.ImportedProduct;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportedProductRepository extends JpaRepository<ImportedProduct, Long> {

    List<ImportedProduct> findByDeletedAtIsNullOrderByIdDesc();

    Optional<ImportedProduct> findByDeletedAtIsNullAndCategoryId(Long id);

    List<ImportedProduct> findByDeletedAtIsNullAndProductId(Long id);


    List<ImportedProduct> findByDeletedAtIsNullAndStockGreaterThanAndIdOrderByCreatedAtAsc(Integer stock, Long id);

    List<ImportedProduct> findByDeletedAtIsNullAndStockAndIdOrderByCreatedAtAsc(Integer stock, Long id);

    List<ImportedProduct> findByDeletedAtIsNullAndBrandId(Long brandId);

    List<ImportedProduct> findByDeletedAtIsNullAndStockGreaterThan(Integer stock);

    List<ImportedProduct> findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(Long productId, Integer stock);

    List<Integer> findStockByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(Long productId, Integer stock);

    List<ImportedProduct> findByDeletedAtIsNullAndBrand_IdAndStockGreaterThan(Long brandId, Integer stock);

    List<ImportedProduct> findByDeletedAtIsNullAndCategory_IdAndStockGreaterThan(Long categoryId, Integer stock);

    List<ImportedProduct> findByDeletedAtIsNullAndColor_IdAndStockGreaterThan(Long colorId, Integer stock);

    List<ImportedProduct> findByDeletedAtIsNullAndSize_IdAndStockGreaterThan(Long sizeId, Integer stock);

    List<ImportedProduct> findByDeletedAtIsNullAndMaterial_IdAndStockGreaterThan(Long materialId, Integer stock);
}
