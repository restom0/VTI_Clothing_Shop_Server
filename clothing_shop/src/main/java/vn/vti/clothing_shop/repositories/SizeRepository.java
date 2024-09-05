package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Size;

import java.util.List;
import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size,Long> {
    @Override
    @Query("SELECT s FROM Size s WHERE s.deleted_at IS NULL")
    @NotNull
    List<Size> findAll();

    @Query("SELECT s FROM Size s WHERE s.size = :size AND s.deleted_at IS NULL")
    Optional<Size> findBySize(String size);

    @Query("SELECT s FROM Size s WHERE s.category_id = :id AND s.deleted_at IS NULL")
    Optional<Size> findByCategory_id(Long id);
}

