package vn.vti.clothing_shop.repositories;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.OnSaleProduct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OnSaleProductRepository extends JpaRepository<OnSaleProduct,Long> {

    @Query("SELECT onSaleProduct " +
            "FROM OnSaleProduct onSaleProduct " +
            "JOIN onSaleProduct.input_sale_id is " +
            "JOIN onSaleProduct.product_id.product_id product " +
            "WHERE product.id = ?1 " +
            "AND product.deleted_at IS NULL " +
            "AND onSaleProduct.deleted_at IS NULL " +
            "AND is.available_date <= CURRENT_TIMESTAMP " +
            "AND (is.end_date IS NULL OR is.end_date >= CURRENT_TIMESTAMP)")
    List<OnSaleProduct> findAllByProductId(Long id);
    @Query("SELECT onSaleProduct " +
            "FROM OnSaleProduct onSaleProduct " +
            "JOIN onSaleProduct.input_sale_id inputSale " +
            "WHERE inputSale.end_date IS NULL " +
            "AND inputSale.available_date <= CURRENT_TIMESTAMP " +
            "AND onSaleProduct.deleted_at IS NULL " +
            "ORDER BY onSaleProduct.id DESC")
    List<OnSaleProduct> findAllAvailableByNullEnd();

    @Query("SELECT onSaleProduct " +
            "FROM OnSaleProduct onSaleProduct " +
            "JOIN onSaleProduct.input_sale_id inputSale " +
            "WHERE inputSale.end_date IS NOT NULL " +
            "AND inputSale.available_date <= CURRENT_TIMESTAMP " +
            "AND inputSale.end_date >= CURRENT_TIMESTAMP " +
            "AND onSaleProduct.deleted_at IS NULL")
    List<OnSaleProduct> findAllAvailableByNotNullEnd();

    @Query("SELECT onSaleProduct " +
            "FROM OnSaleProduct onSaleProduct " +
            "JOIN onSaleProduct.input_sale_id inputSale " +
            "WHERE inputSale.id = ?1 " +
            "AND inputSale.deleted_at IS NULL " +
            "AND onSaleProduct.deleted_at IS NULL")
    List<OnSaleProduct> findByInputSaleId(Long id);

    @Query("SELECT onSaleProduct " +
            "FROM OnSaleProduct onSaleProduct " +
            "JOIN onSaleProduct.product_id product " +
            "JOIN onSaleProduct.input_sale_id inputSale " +
                "WHERE product.id = ?1 " +
                "AND product.deleted_at IS NULL " +
                "AND onSaleProduct.deleted_at IS NULL " +
                "AND inputSale.deleted_at IS NULL " +
                "AND inputSale.available_date <= ?2 " +
                "AND inputSale.end_date IS NOT NULL " +
                "AND NOT (inputSale.available_date >= ?3 OR inputSale.end_date <= ?2) ")
    Optional<OnSaleProduct> findByProductIdAndAvailableDateAndNotNullEndDate(Long id, LocalDateTime availableDate, LocalDateTime endDate);

    @Query("SELECT onSaleProduct " +
            "FROM OnSaleProduct onSaleProduct " +
            "JOIN onSaleProduct.product_id product " +
            "JOIN onSaleProduct.input_sale_id inputSale " +
                "WHERE product.id = ?1 " +
                "AND product.deleted_at IS NULL " +
                "AND onSaleProduct.deleted_at IS NULL " +
                "AND inputSale.deleted_at IS NULL " +
                "AND inputSale.end_date IS NULL ")
    Optional<OnSaleProduct> findByProductIdAndAvailableDateAndNullEndDate(Long id, @Future(message = "Available date must be in the future") @NotNull(message = "Available date is required") LocalDateTime availableDate);
}
