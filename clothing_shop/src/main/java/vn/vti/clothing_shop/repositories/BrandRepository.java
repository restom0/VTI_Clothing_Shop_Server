package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.entities.Brand;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
	/** Finds by deleted at is null order by id desc. */
	List<Brand> findByDeletedAtIsNullOrderByIdDesc();

	/** Finds by deleted at is null and id. */
	Optional<Brand> findByDeletedAtIsNullAndId(Long id);

	/** Handles exists by deleted at is null and name. */
	boolean existsByDeletedAtIsNullAndName(String name);

	/** Handles exists by deleted at is null and id not and name. */
	boolean existsByDeletedAtIsNullAndIdNotAndName(Long id, String name);

	/** Counts by deleted at is null. */
	long countByDeletedAtIsNull();
}
