package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.entities.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	/** Finds all by deleted at is null order by id desc. */
	List<Category> findAllByDeletedAtIsNullOrderByIdDesc();

	/** Finds by deleted at is null and id. */
	Optional<Category> findByDeletedAtIsNullAndId(Long id);

	/** Finds by deleted at is null and name. */
	Optional<Category> findByDeletedAtIsNullAndName(String name);

	/** Counts by deleted at is null. */
	long countByDeletedAtIsNull();

	/** Handles exists by deleted at is null and name. */
	boolean existsByDeletedAtIsNullAndName(String name);

	/** Handles exists by deleted at is null and name and id not. */
	boolean existsByDeletedAtIsNullAndNameAndIdNot(String name, Long id);
}
