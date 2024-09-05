package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Color;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color,Long> {
    @Query("SELECT c FROM Color c WHERE c.color_name=:color_name AND c.deleted_at IS NULL ORDER BY c.id DESC")
    Optional<Color> findByName(String color_name);

    @Query("SELECT c FROM Color c WHERE c.color_code=?1 AND c.deleted_at IS NULL")
    Optional<Color> findByColor(String color_code);

    @Query("SELECT c FROM Color c WHERE c.category_id=?1 AND c.deleted_at IS NULL")
    Optional<Color> findByCategory_id(Long id);

    @Override
    @Query("SELECT c FROM Color c WHERE c.deleted_at IS NULL ORDER BY c.id DESC")
    @NotNull
    List<Color> findAll();
}
