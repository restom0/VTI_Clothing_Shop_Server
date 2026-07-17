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

	/** Finds by deleted at is null order by id desc. */
	List<OrderItem> findByDeletedAtIsNullOrderByIdDesc();

	/** Counts by deleted at is null and order payment status. */
	Long countByDeletedAtIsNullAndOrder_PaymentStatus(PaymentStatus paymentStatus);

	/** Finds by deleted at is null and id. */
	Optional<OrderItem> findByDeletedAtIsNullAndId(Long id);

	/** Finds by deleted at is null and order id. */
	List<OrderItem> findByDeletedAtIsNullAndOrder_Id(Long orderId);

	/** Finds by deleted at is null and id and order id. */
	Optional<OrderItem> findByDeletedAtIsNullAndIdAndOrder_Id(Long id, Long orderId);

	/** Finds by deleted at is null and product id and order id. */
	Optional<OrderItem> findByDeletedAtIsNullAndProduct_IdAndOrder_Id(Long productId, Long orderId);

	/** Handles sum quantity by payment status. */
	@Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order.paymentStatus = :status AND oi.deletedAt IS NULL")
	Optional<Long> sumQuantityByPaymentStatus(PaymentStatus status);
}
