package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.ImportedProduct;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportedProductRepository extends JpaRepository<ImportedProduct,Long> {
    @Override
    @Query("SELECT ip FROM ImportedProduct ip " +
            "WHERE ip.deleted_at IS NULL ORDER BY ip.id DESC")
    @NotNull
    List<ImportedProduct> findAll();

    @Query("SELECT ip FROM ImportedProduct ip WHERE ip.product_id.category_id = ?1 AND ip.deleted_at IS NULL")
    Optional<ImportedProduct> findByCategoryId(Long id);

    @Query("SELECT ip FROM ImportedProduct ip WHERE ip.product_id.id = ?1 AND ip.deleted_at IS NULL")
    List<ImportedProduct> findByProductId(Long id);


    @Query("SELECT ip " +
            "FROM ImportedProduct ip " +
            "WHERE ip.id = ?1 " +
                "AND ip.deleted_at IS NULL " +
                "AND ip.stock > 0" +
            "ORDER BY ip.created_at ASC")
    List<ImportedProduct> findByProductIdWithPositiveStock(Long id);

    @Query("SELECT ip " +
            "FROM ImportedProduct ip " +
            "WHERE ip.id = ?1 " +
            "AND ip.deleted_at IS NULL " +
            "AND ip.stock = 0" +
            "ORDER BY ip.created_at DESC")
    List<ImportedProduct> findByProductIdWithZeroStock(Long id);

    @Query("SELECT ip FROM ImportedProduct ip WHERE ip.product_id.brand_id = ?1 AND ip.deleted_at IS NULL")
    List<ImportedProduct> findByBrandId(Long id);

    @Query("SELECT ip " +
            "FROM ImportedProduct ip " +
            "WHERE ip.stock > 0 " +
                "AND ip.deleted_at IS NULL")
    List<ImportedProduct> findAllWithPositiveStock();

    @Query("SELECT ip " +
            "FROM ImportedProduct ip " +
            "WHERE ip.stock > 0 " +
                "AND ip.product_id.id = ?1 " +
                "AND ip.deleted_at IS NULL")
    List<ImportedProduct> findAllWithPositiveStockByProductId(Long id);

    @Query("SELECT ip " +
            "FROM ImportedProduct ip " +
            "WHERE ip.stock > 0 " +
                "AND ip.product_id.brand_id.id = ?1 " +
                "AND ip.deleted_at IS NULL")
    List<ImportedProduct> findAllWithPositiveStockByBrandId(Long id);

    @Query("SELECT ip " +
            "FROM ImportedProduct ip " +
            "WHERE ip.stock > 0 " +
                "AND ip.product_id.category_id.id = ?1 " +
                "AND ip.deleted_at IS NULL")
    List<ImportedProduct> findAllWithPositiveStockByCategoryId(Long id);

    @Query("SELECT ip " +
            "FROM ImportedProduct ip " +
            "WHERE ip.stock > 0 " +
                "AND ip.color_id.id = ?1 " +
                "AND ip.deleted_at IS NULL")
    List<ImportedProduct> findAllWithPositiveStockByColorId(Long id);

    @Query("SELECT ip " +
            "FROM ImportedProduct ip " +
            "WHERE ip.stock > 0 " +
                "AND ip.size_id.id = ?1 " +
                "AND ip.deleted_at IS NULL")
    List<ImportedProduct> findAllWithPositiveStockBySizeId(Long id);

    @Query("SELECT ip " +
            "FROM ImportedProduct ip " +
            "WHERE ip.stock > 0 " +
                "AND ip.material_id.id = ?1 " +
                "AND ip.deleted_at IS NULL")
    List<ImportedProduct> findAllWithPositiveStockByMaterialId(Long id);
}
