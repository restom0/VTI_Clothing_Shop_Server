package vn.vti.clothing_shop.repositories;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.OnSaleProduct;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OnSaleProductRepository extends JpaRepository<OnSaleProduct, Long> {

    Optional<OnSaleProduct> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT onSaleProduct " +
            "FROM OnSaleProduct onSaleProduct " +
            "JOIN onSaleProduct.input_sale_id is " +
            "JOIN onSaleProduct.productId.productId product " +
            "WHERE product.id = ?1 " +
            "AND product.deletedAt IS NULL " +
            "AND onSaleProduct.deletedAt IS NULL " +
            "AND is.available_date <= CURRENT_TIMESTAMP " +
            "AND (is.end_date IS NULL OR is.end_date >= CURRENT_TIMESTAMP)")
    List<OnSaleProduct> findAllByProductId(Long id);

    List<OnSaleProduct> findByInputSale_IdAndInputSale_StartDateLessThanEqualAndDeletedAtIsNullAndInputSale_DeletedAtIsNullOrderByIdDesc(Long inputSaleId, LocalDate startDate);

    @Query("SELECT onSaleProduct " +
            "FROM OnSaleProduct onSaleProduct " +
            "JOIN onSaleProduct.productId product " +
            "JOIN onSaleProduct.input_sale_id inputSale " +
            "WHERE product.id = ?1 " +
            "AND product.deletedAt IS NULL " +
            "AND onSaleProduct.deletedAt IS NULL " +
            "AND inputSale.deletedAt IS NULL " +
            "AND inputSale.available_date <= ?2 " +
            "AND inputSale.end_date IS NOT NULL " +
            "AND NOT (inputSale.available_date >= ?3 OR inputSale.end_date <= ?2) ")
    Optional<OnSaleProduct> findByProductIdAndAvailableDateAndNotNullEndDate(Long id, LocalDate availableDate, LocalDate endDate);

    @Query("SELECT onSaleProduct " +
            "FROM OnSaleProduct onSaleProduct " +
            "JOIN onSaleProduct.productId product " +
            "JOIN onSaleProduct.input_sale_id inputSale " +
            "WHERE product.id = ?1 " +
            "AND product.deletedAt IS NULL " +
            "AND onSaleProduct.deletedAt IS NULL " +
            "AND inputSale.deletedAt IS NULL " +
            "AND inputSale.end_date IS NULL ")
    Optional<OnSaleProduct> findByProductIdAndAvailableDateAndNullEndDate(Long id, @Future(message = "Available date must be in the future") @NotNull(message = "Available date is required") LocalDate availableDate);

    List<OnSaleProduct> findDistinctByDeletedAtIsNull();
}
