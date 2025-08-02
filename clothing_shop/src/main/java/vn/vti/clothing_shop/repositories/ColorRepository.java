package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Color;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

    Optional<Color> findByDeletedAtIsNullAndNameOrderByIdDesc(String colorName);

    Optional<Color> findByDeletedAtIsNullAndColor(String colorCode);

    Optional<Color> findByDeletedAtIsNullAndCategory_id(Long categoryId);

    List<Color> findAllByDeletedAtIsNull();
}
