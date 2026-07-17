package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.entities.Voucher;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
	/** Finds by deleted at is null order by id desc. */
	List<Voucher> findByDeletedAtIsNullOrderByIdDesc();

	/** Handles exists by deleted at is null and code. */
	boolean existsByDeletedAtIsNullAndCode(String code);

	/** Finds by deleted at is null and code. */
	Optional<Voucher> findByDeletedAtIsNullAndCode(String code);

	/** Finds by deleted at is null and stock greater than and available date less than equal and end date greater than equal. */
	List<Voucher> findByDeletedAtIsNullAndStockGreaterThanAndAvailableDateLessThanEqualAndEndDateGreaterThanEqual(
			Integer stock, Long availableDate, Long endDate);

	/** Finds by deleted at is null and id. */
	Optional<Voucher> findByDeletedAtIsNullAndId(Long id);
}
