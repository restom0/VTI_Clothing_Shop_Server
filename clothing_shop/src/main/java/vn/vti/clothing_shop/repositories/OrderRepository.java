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
	/** Handles sum total price by month and year. */
	@Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.deletedAt IS NULL AND o.paymentStatus = 'COMPLETED' AND month(o.createdAt) = ?1 AND year(o.createdAt) = ?2")
	Long sumTotalPriceByMonthAndYear(Integer month, Integer year);

	/** Finds by deleted at is null order by id desc. */
	List<Order> findByDeletedAtIsNullOrderByIdDesc();

	/** Finds by deleted at is null and user id. */
	List<Order> findByDeletedAtIsNullAndUser_Id(Long userId);

	/** Finds by deleted at is null and id. */
	Optional<Order> findByDeletedAtIsNullAndId(Long id);

	/** Finds by deleted at is null and id and user id. */
	Optional<Order> findByDeletedAtIsNullAndIdAndUser_Id(Long id, Long userId);

	/** Finds by deleted at is null and user id and payment status. */
	Optional<Order> findByDeletedAtIsNullAndUser_IdAndPaymentStatus(Long userId, PaymentStatus paymentStatus);

	/** Counts by deleted at is null. */
	long countByDeletedAtIsNull();

	/** Handles sum total price by deleted at is null and payment status. */
	@Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.deletedAt IS NULL AND o.paymentStatus = :status")
	Long sumTotalPriceByDeletedAtIsNullAndPaymentStatus(PaymentStatus paymentStatus);

	/** Counts completed order by month. */
	@Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.deletedAt IS NULL AND o.paymentStatus = 'COMPLETED' GROUP BY year(o.createdAt), month(o.createdAt)")
	List<Long> countCompletedOrderByMonth();

	/** Counts by deleted at is null and payment status. */
	Long countByDeletedAtIsNullAndPaymentStatus(PaymentStatus paymentStatus);

	/** Finds by deleted at is null and payment status. */
	@Query("SELECT o FROM Order o WHERE o.deletedAt IS NULL AND o.paymentStatus = 'COMPLETED'")
	List<Order> findByDeletedAtIsNullAndPaymentStatus(PaymentStatus paymentStatus);

	/** Finds by deleted at is null and order code and user id. */
	Optional<Order> findByDeletedAtIsNullAndOrderCodeAndUser_Id(Long orderCode, Long userId);
}
