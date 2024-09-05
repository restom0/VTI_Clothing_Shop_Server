package vn.vti.clothing_shop.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.vti.clothing_shop.entities.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>{
    @Override
    @Query("SELECT c FROM Comment c WHERE c.deleted_at IS NULL ORDER BY c.id DESC")
    @NotNull
    List<Comment> findAll();

    @Query("SELECT c FROM Comment c WHERE c.product_id = ?1 AND c.deleted_at IS NULL ORDER BY c.created_at DESC")
    List<Comment> findByProductId(Long product_id);

    @Query("SELECT c FROM Comment c WHERE c.id = ?1 AND c.product_id=?2 AND c.user_id=?3 AND c.deleted_at IS NULL")
    Optional<Comment> findByProductIdAndId(Long id, Long product_id, Long user_id);

    @Query("SELECT c FROM Comment c WHERE c.id = ?1 AND c.user_id=?2 AND c.deleted_at IS NULL")
    Optional<Comment> findByUserIdAndId(Long id, Long user_id);

    @Query("SELECT c.product_id, SUM(c.star) FROM Comment c WHERE c.deleted_at IS NULL GROUP BY c.product_id ORDER BY SUM(c.star) DESC")
    List<Object[]> sumRatingByProductId();
}
