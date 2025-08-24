package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Size;

import java.util.List;
import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    List<Size> findByDeletedAtIsNull();

    Optional<Size> findByName(String name);

    Optional<Size> findByDeletedAtIsNullAndCategory_Id(Long id);

    Optional<Size> findByDeletedAtIsNullAndSizeAndHeightAndWeight(String size, String height, String weight);
}

