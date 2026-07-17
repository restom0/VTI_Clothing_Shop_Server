package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.entities.Material;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

	/** Finds by deleted at is null. */
	List<Material> findByDeletedAtIsNull();

	/** Finds by deleted at is null and name. */
	Optional<Material> findByDeletedAtIsNullAndName(String name);

	/** Finds by category id and deleted at is null. */
	Optional<Material> findByCategory_IdAndDeletedAtIsNull(Long id);

}

