package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.CommentCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CommentUpdateRequest;
import vn.vti.clothing_shop.entities.Comment;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface CommentService {
	/** Gets all comments. */
	List<Comment> getAllComments();

	/** Gets comment by product id. */
	List<Comment> getCommentByProductId(Long productId);

	/** Creates comment. */
	void createComment(Long userId, CommentCreateRequest commentCreateRequest) throws WrapperException;

	/** Updates comment. */
	void updateComment(Long id, Long userId, CommentUpdateRequest commentUpdateRequest) throws WrapperException;

	/** Deletes comment. */
	void deleteComment(Long id, Long userId) throws WrapperException;
}
