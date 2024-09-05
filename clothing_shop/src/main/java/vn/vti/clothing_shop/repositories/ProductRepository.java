package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    @Override
    @Query("SELECT p FROM Product p WHERE p.deleted_at IS NULL ORDER BY p.id DESC")
    @NotNull
    List<Product> findAll();
    @Override
    @Query("SELECT COUNT(p) FROM Product p WHERE p.deleted_at IS NULL")
    long count();
}
