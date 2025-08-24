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

    @Query("SELECT SUM(o.total_price) FROM Order o WHERE o.deletedAt IS NULL AND o.payment_status = 'COMPLETED' AND month(o.created_at) = ?1 AND year(o.created_at) = ?2")
    Long sumTotalPriceByMonthAndYear(Integer month, Integer year);

    List<Order> findByDeletedAtIsNullOrderByIdDesc();

    List<Order> findByDeletedAtIsNullAndUser_Id(Long userId);

    Optional<Order> findByDeletedAtIsNullAndId(Long id);

    Optional<Order> findByDeletedAtIsNullAndIdAndUser_Id(Long id, Long userId);

    Optional<Order> findByDeletedAtIsNullAndUser_IdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);

    long countByDeletedAtIsNull();

    Long sumTotalPriceByDeletedAtIsNullAndPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT SUM(o.total_price) FROM Order o WHERE o.deleted_at IS NULL AND o.payment_status = 'COMPLETED' GROUP BY year(o.created_at), month(o.created_at)")
    List<Long> countCompletedOrderByMonth();

    Long countByDeletedAtIsNullAndPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT o FROM Order o WHERE o.deleted_at IS NULL AND o.payment_status = 'COMPLETED'")
    List<Order> findByDeletedAtIsNullAndPaymentStatus(PaymentStatus paymentStatus);

    Optional<Order> findByDeletedAtIsNullAndOrderCodeAndUser_Id(Long orderCode, Long userId);
}
