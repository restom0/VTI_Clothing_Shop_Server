package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Voucher;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher,Long> {
    @Override
    @Query("SELECT v FROM Voucher v WHERE v.deleted_at IS NULL ORDER BY v.id DESC")
    @NotNull
    List<Voucher> findAll();

    @Query("SELECT v FROM Voucher v WHERE v.code = ?1 AND v.deleted_at IS NULL")
    Optional<Voucher> findByCode(String code);

    @Query("SELECT v FROM Voucher v " +
            "WHERE v.stock > 0 " +
            "AND v.deleted_at IS NULL " +
            "AND v.available_date <= CURRENT_TIMESTAMP " +
            "AND v.expired_date >= CURRENT_TIMESTAMP")
    List<Voucher> findAllWithPositiveStockAndAvailable();
    @Override
    @Query("SELECT v FROM Voucher v WHERE v.id = ?1 AND v.deleted_at IS NULL")
    @NotNull
    Optional<Voucher> findById(@NotNull Long id);


}
