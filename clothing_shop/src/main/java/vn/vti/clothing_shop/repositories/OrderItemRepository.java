package vn.vti.clothing_shop.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.constants.PaymentStatus;
import vn.vti.clothing_shop.entities.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	List<OrderItem> findByDeletedAtIsNullOrderByIdDesc();

	Long countByDeletedAtIsNullAndOrder_PaymentStatus(PaymentStatus paymentStatus);

	Optional<OrderItem> findByDeletedAtIsNullAndId(Long id);

	List<OrderItem> findByDeletedAtIsNullAndOrder_Id(Long orderId);

	Optional<OrderItem> findByDeletedAtIsNullAndIdAndOrder_Id(Long id, Long orderId);

	@Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.paymentStatus = :status AND oi.deletedAt IS NULL")
	Optional<Long> sumQuantityByPaymentStatus(PaymentStatus status);
}
