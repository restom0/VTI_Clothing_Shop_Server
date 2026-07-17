package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.vti.clothing_shop.entities.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	/** Gets all by deleted at is null. */
	List<Comment> getAllByDeletedAtIsNull();

	/** Finds by deleted at is null and product id order by created at desc. */
	List<Comment> findByDeletedAtIsNullAndProductIdOrderByCreatedAtDesc(Long productId);

	/** Finds by deleted at is null and id. */
	Optional<Comment> findByDeletedAtIsNullAndId(Long id);

	/** Finds by deleted at is null and id and product id and user id. */
	Optional<Comment> findByDeletedAtIsNullAndIdAndProductIdAndUserId(Long id, Long productId, Long userId);

	/** Finds by deleted at is null and user id and id. */
	Optional<Comment> findByDeletedAtIsNullAndUserIdAndId(Long userId, Long id);

	/** Handles sum rating by product id. */
	@Query("SELECT c.product, SUM(c.star) FROM Comment c WHERE c.deletedAt IS NULL GROUP BY c.product ORDER BY SUM(c.star) DESC")
	List<Object[]> sumRatingByProductId();

}
