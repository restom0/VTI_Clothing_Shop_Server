package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.OrderItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
    @Override
    @Query("SELECT oi FROM OrderItem oi WHERE oi.deleted_at IS NULL ORDER BY oi.id DESC")
    @NotNull
    List<OrderItem> findAll();

    @Query("SELECT COUNT(oi) FROM OrderItem oi JOIN oi.order_id o WHERE oi.deleted_at IS NULL AND o.payment_status = 'COMPLETED'")
    Long countAllByCompletedOrder();

    @Override
    @Query("SELECT oi FROM OrderItem oi WHERE oi.id = ?1 AND oi.deleted_at IS NULL")
    @NotNull
    Optional<OrderItem> findById(@NotNull Long id);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order_id.id = ?1 AND oi.deleted_at IS NULL")
    List<OrderItem> findAllByOrderId(Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.id = ?1 AND oi.order_id.id = ?2 AND oi.deleted_at IS NULL")
    Optional<OrderItem> findByIdAndOrderId(Long id, Long orderId);

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi JOIN Order o " +
            "WHERE o.payment_status = COMPLETED AND oi.deleted_at IS NULL")
    Optional<Long> sumQuantitiesByOrderId(Long orderId);
}
