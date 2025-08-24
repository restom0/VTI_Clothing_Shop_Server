package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.InputSale;

import java.util.List;

@Repository
public interface InputSaleRepository extends JpaRepository<InputSale, Long> {
    List<InputSale> findByDeletedAtIsNullOrderByIdDesc();

    List<InputSale> findByDeletedAtIsNull();
}
