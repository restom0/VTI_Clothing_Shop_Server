package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Material;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    List<Material> findByDeletedAtIsNull();

    Optional<Material> findByDeletedAtIsNullAndName(String name);

    Optional<Material> findByCategory_IdAndDeletedAtIsNull(Long id);

}

