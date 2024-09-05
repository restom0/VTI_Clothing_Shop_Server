package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Override
    @Query("SELECT c FROM Category c WHERE c.deleted_at IS NULL ORDER BY c.id DESC")
    @NotNull
    List<Category> findAll();

    @Override
    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.deleted_at IS NULL")
    @NotNull
    Optional<Category> findById(@NotNull Long id);

    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.deleted_at IS NULL")
    Optional<Category> findByName(String name);

    @Override
    @Query("SELECT COUNT(c) FROM Category c WHERE c.deleted_at IS NULL")
    long count();
}
