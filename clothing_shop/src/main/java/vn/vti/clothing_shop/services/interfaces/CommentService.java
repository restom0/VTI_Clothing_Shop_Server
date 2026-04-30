package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.CommentCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CommentUpdateRequest;
import vn.vti.clothing_shop.entities.Comment;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface CommentService {
	List<Comment> getAllComments();

	List<Comment> getCommentByProductId(Long productId);

	void createComment(Long userId, CommentCreateRequest commentCreateRequest) throws WrapperException;

	void updateComment(Long id, Long userId, CommentUpdateRequest commentUpdateRequest) throws WrapperException;

	void deleteComment(Long id, Long userId) throws WrapperException;
}
