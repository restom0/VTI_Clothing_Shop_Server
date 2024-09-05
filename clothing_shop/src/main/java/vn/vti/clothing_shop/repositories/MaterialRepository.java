package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Brand;
import vn.vti.clothing_shop.entities.Material;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material,Long> {
    @Override
    @Query("SELECT m FROM Material m WHERE m.deleted_at IS NULL")
    @NotNull
    List<Material> findAll();

    @Query("SELECT m FROM Material m WHERE m.name=:name AND m.deleted_at IS NULL")
    Optional<Material> findByName(String name);

    @Query("SELECT m FROM Material m WHERE m.category_id=:id AND m.deleted_at IS NULL")
    Optional<Material> findByCategory_id(Long id);
}

