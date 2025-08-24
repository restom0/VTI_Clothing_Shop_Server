package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedAtIsNullOrderByIdDesc();

    long countByDeletedAtIsNull();

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);
}
