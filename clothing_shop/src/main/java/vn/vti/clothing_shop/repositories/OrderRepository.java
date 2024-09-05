package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query("SELECT SUM(o.total_price) FROM Order o WHERE o.deleted_at IS NULL AND o.payment_status = 'COMPLETED' AND month(o.created_at) = ?1 AND year(o.created_at) = ?2")
    Long sumTotalPriceByMonthAndYear(Integer month, Integer year);
    @Override
    @Query("SELECT o FROM Order o WHERE o.deleted_at IS NULL ORDER BY o.id DESC")
    @NotNull
    List<Order> findAll();

    @Query("SELECT o FROM Order o WHERE o.deleted_at IS NULL AND o.user_id.id = ?1")
    List<Order> findAllByUserId(Long userId);

    @Override
    @Query("SELECT o FROM Order o WHERE o.deleted_at IS NULL AND o.id = ?1")
    @NotNull
    Optional<Order> findById(@NotNull Long id);

//    @Query("SELECT o FROM Order o WHERE o.deleted_at IS NULL AND o.id = ?1")
//    Order findOrderById(Long id);

    @Query("SELECT o FROM Order o WHERE o.deleted_at IS NULL AND o.id = ?1 AND o.user_id.id = ?2")
    Optional<Order> findOrderByIdAndUserId(Long id,Long userId);

    @Query("SELECT o FROM Order o WHERE o.deleted_at IS NULL AND o.user_id.id = ?1 AND o.payment_status = NOT_CONFIRMED")
    Optional<Order>findByUserIdWithNOT_CONFIRMEDStatus(Long userId);

    @Override
    @Query("SELECT COUNT(o) FROM Order o WHERE o.deleted_at IS NULL")
    long count();

    @Query("SELECT SUM(o.total_price) FROM Order o WHERE o.deleted_at IS NULL AND o.payment_status = 'COMPLETED'")
    Long sumTotalPrice();

    @Query("SELECT SUM(o.total_price) FROM Order o WHERE o.deleted_at IS NULL AND o.payment_status = 'COMPLETED' GROUP BY year(o.created_at), month(o.created_at)")
    List<Long> countCompletedOrderByMonth();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.deleted_at IS NULL AND o.payment_status = 'COMPLETED'")
    Long countCompletedOrder();

    @Query("SELECT o FROM Order o WHERE o.deleted_at IS NULL AND o.payment_status = 'COMPLETED'")
    List<Order> findAllCompletedOrder();

    @Query("SELECT o FROM Order o WHERE o.order_code=?1 AND o.user_id.id=?2 AND o.deleted_at IS NULL AND o.payment_status = 'NOT_CONFIRMED'")
    Optional<Order> findOrderByOrderCodeAndUserId(Long orderCode, Long userId);

}
