package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Voucher;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findByDeletedAtIsNullOrderByIdDesc();

    boolean existsByDeletedAtIsNullAndCode(String code);

    Optional<Voucher> findByDeletedAtIsNullAndCode(String code);

    List<Voucher> findByDeletedAtIsNullAndStockGreaterThanAndAvailableDateGreaterThanEqualAndEndDateLessThanEqual(
            Integer stock, Long availableDate, Long endDate);

    Optional<Voucher> findByDeletedAtIsNullAndId(Long id);
}
