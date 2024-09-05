package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.InputSale;

import java.util.List;

@Repository
public interface InputSaleRepository extends JpaRepository<InputSale,Long> {
    @Override
    @Query("SELECT i FROM InputSale i WHERE i.deleted_at IS NULL ORDER BY i.id DESC")
    @NotNull
    List<InputSale> findAll();


}
