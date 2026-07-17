package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.entities.Size;

import java.util.List;
import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
	/** Finds by deleted at is null. */
	List<Size> findByDeletedAtIsNull();

	/** Finds by name. */
	Optional<Size> findByName(String name);

	/** Finds by deleted at is null and category id. */
	Optional<Size> findByDeletedAtIsNullAndCategory_Id(Long id);

	/** Finds by deleted at is null and name and height and weight. */
	Optional<Size> findByDeletedAtIsNullAndNameAndHeightAndWeight(String name, String height, String weight);
}

