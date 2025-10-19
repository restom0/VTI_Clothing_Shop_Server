package vn.vti.clothing_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> getAllByDeletedAtIsNull();

    List<Comment> findByDeletedAtIsNullAndProductIdOrderByCreatedAtDesc(Long productId);

    Optional<Comment> findByDeletedAtIsNullAndId(Long id);

    Optional<Comment> findByDeletedAtIsNullAndIdAndProductIdAndUserId(Long id, Long productId, Long userId);

    Optional<Comment> findByDeletedAtIsNullAndUserIdAndId(Long userId, Long id);

    @Query("SELECT c.product, SUM(c.star) FROM Comment c WHERE c.deletedAt IS NULL GROUP BY c.product ORDER BY SUM(c.star) DESC")
    List<Object[]> sumRatingByProductId();

}
