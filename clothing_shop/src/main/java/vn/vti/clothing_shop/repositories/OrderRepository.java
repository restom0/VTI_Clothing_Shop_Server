package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.constants.PaymentStatus;
import vn.vti.clothing_shop.entities.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.deletedAt IS NULL AND o.paymentStatus = 'COMPLETED' AND month(o.createdAt) = ?1 AND year(o.createdAt) = ?2")
    Long sumTotalPriceByMonthAndYear(Integer month, Integer year);

    List<Order> findByDeletedAtIsNullOrderByIdDesc();

    List<Order> findByDeletedAtIsNullAndUser_Id(Long userId);

    Optional<Order> findByDeletedAtIsNullAndId(Long id);

    Optional<Order> findByDeletedAtIsNullAndIdAndUser_Id(Long id, Long userId);

    Optional<Order> findByDeletedAtIsNullAndUser_IdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);

    long countByDeletedAtIsNull();

	@Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.deletedAt IS NULL AND o.paymentStatus = :status")
    Long sumTotalPriceByDeletedAtIsNullAndPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.deletedAt IS NULL AND o.paymentStatus = 'COMPLETED' GROUP BY year(o.createdAt), month(o.createdAt)")
    List<Long> countCompletedOrderByMonth();

    Long countByDeletedAtIsNullAndPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL AND o.paymentStatus = 'COMPLETED'")
    List<Order> findByDeletedAtIsNullAndPaymentStatus(PaymentStatus paymentStatus);

    Optional<Order> findByDeletedAtIsNullAndOrderCodeAndUser_Id(Long orderCode, Long userId);
}
