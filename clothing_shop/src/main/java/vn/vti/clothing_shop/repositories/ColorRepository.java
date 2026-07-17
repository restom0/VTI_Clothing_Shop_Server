package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.entities.Color;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

	/** Finds by deleted at is null and name order by id desc. */
	Optional<Color> findByDeletedAtIsNullAndNameOrderByIdDesc(String colorName);

	/** Finds by deleted at is null and code. */
	Optional<Color> findByDeletedAtIsNullAndCode(String code);

	/** Finds by deleted at is null and category id. */
	Optional<Color> findByDeletedAtIsNullAndCategory_Id(Long categoryId);

	/** Finds by deleted at is null. */
	List<Color> findByDeletedAtIsNull();

	/** Finds by deleted at is null and id. */
	Optional<Color> findByDeletedAtIsNullAndId(Long id);
}
