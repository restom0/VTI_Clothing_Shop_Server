package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Voucher;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    List<Voucher> findByDeletedAtIsNullOrderByIdDesc();

    boolean existByDeletedAtIsNullAndCode(String code);

    Optional<Voucher> findByDeletedAtIsNullAndCode(String code);

    List<Voucher> findByDeletedAtIsNullAndStockGreaterThanAndAvailableDateGreaterThanEqualAndExpiredDateLessThanEqual(
            Integer stock, Long availableDate, Long expiredDate);

    Optional<Voucher> findByDeletedAtIsNullAndId(Long id);
}
