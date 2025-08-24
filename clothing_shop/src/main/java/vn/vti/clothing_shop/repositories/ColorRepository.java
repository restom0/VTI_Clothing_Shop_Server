package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Color;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

    Optional<Color> findByDeletedAtIsNullAndNameOrderByIdDesc(String colorName);

    Optional<Color> findByDeletedAtIsNullAndCode(String code);

    Optional<Color> findByDeletedAtIsNullAndCategory_Id(Long categoryId);

    List<Color> findByDeletedAtIsNull();

    Optional<Color> findByDeletedAtIsNullAndId(Long id);
}
