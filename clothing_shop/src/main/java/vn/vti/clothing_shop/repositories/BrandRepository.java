package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Brand;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    List<Brand> getAllByDeletedAtIsNullOrderByIdDesc();

    Optional<Brand> findByDeletedAtIsNullAndId(Long id);

    Optional<Brand> findByDeletedAtIsNullAndName(String name);

    long countByDeletedAtIsNull();
}
