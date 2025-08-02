package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> getAllByDeletedAtIsNullOrderByIdDesc();

    Optional<Category> findByDeletedAtIsNullAndId(Long id);

    Optional<Category> findByDeletedAtIsNullAndName(String name);

    long countByDeletedAtIsNull();
}
