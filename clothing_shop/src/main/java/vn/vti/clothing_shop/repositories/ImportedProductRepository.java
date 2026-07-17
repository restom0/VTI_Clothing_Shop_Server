package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.entities.ImportedProduct;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportedProductRepository extends JpaRepository<ImportedProduct, Long> {

	/** Finds by deleted at is null order by id desc. */
	List<ImportedProduct> findByDeletedAtIsNullOrderByIdDesc();

	/** Finds by deleted at is null and product category id. */
	Optional<ImportedProduct> findByDeletedAtIsNullAndProduct_Category_Id(Long id);

	/** Finds by deleted at is null and product id. */
	List<ImportedProduct> findByDeletedAtIsNullAndProductId(Long id);

	/** Finds by deleted at is null and stock greater than and id order by created at asc. */
	List<ImportedProduct> findByDeletedAtIsNullAndStockGreaterThanAndIdOrderByCreatedAtAsc(Integer stock, Long id);

	/** Finds by deleted at is null and stock and id order by created at asc. */
	List<ImportedProduct> findByDeletedAtIsNullAndStockAndIdOrderByCreatedAtAsc(Integer stock, Long id);

	/** Finds by deleted at is null and product brand id. */
	List<ImportedProduct> findByDeletedAtIsNullAndProduct_Brand_Id(Long brandId);

	/** Finds by deleted at is null and stock greater than. */
	List<ImportedProduct> findByDeletedAtIsNullAndStockGreaterThan(Integer stock);

	/** Finds by deleted at is null and product id and stock greater than. */
	List<ImportedProduct> findByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(Long productId, Integer stock);

	/** Finds stock by deleted at is null and product id and stock greater than. */
	List<Integer> findStockByDeletedAtIsNullAndProduct_IdAndStockGreaterThan(Long productId, Integer stock);

	/** Finds by deleted at is null and product brand id and stock greater than. */
	List<ImportedProduct> findByDeletedAtIsNullAndProduct_Brand_IdAndStockGreaterThan(Long brandId, Integer stock);

	/** Finds by deleted at is null and product category id and stock greater than. */
	List<ImportedProduct> findByDeletedAtIsNullAndProduct_Category_IdAndStockGreaterThan(Long categoryId, Integer stock);

	/** Finds by deleted at is null and color id and stock greater than. */
	List<ImportedProduct> findByDeletedAtIsNullAndColor_IdAndStockGreaterThan(Long colorId, Integer stock);

	/** Finds by deleted at is null and size id and stock greater than. */
	List<ImportedProduct> findByDeletedAtIsNullAndSize_IdAndStockGreaterThan(Long sizeId, Integer stock);

	/** Finds by deleted at is null and material id and stock greater than. */
	List<ImportedProduct> findByDeletedAtIsNullAndMaterial_IdAndStockGreaterThan(Long materialId, Integer stock);
}
