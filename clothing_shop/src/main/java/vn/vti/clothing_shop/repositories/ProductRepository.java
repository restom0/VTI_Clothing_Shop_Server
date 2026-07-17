package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.entities.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	/** Finds by deleted at is null order by id desc. */
	List<Product> findByDeletedAtIsNullOrderByIdDesc();

	/** Counts by deleted at is null. */
	long countByDeletedAtIsNull();

	/** Finds by id and deleted at is null. */
	Optional<Product> findByIdAndDeletedAtIsNull(Long id);
}
