package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.dtos.ins.CommentCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CommentUpdateRequest;
import vn.vti.clothing_shop.entities.Comment;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.CommentMapper;
import vn.vti.clothing_shop.repositories.CommentRepository;
import vn.vti.clothing_shop.repositories.ProductRepository;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.services.interfaces.CommentService;
import vn.vti.clothing_shop.utils.TimeUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final CommentMapper commentMapper;

	/** Gets all comments. */
	//@Cacheable(value = "comments")
	@Override
	public List<Comment> getAllComments() {
		return commentRepository.findAll();
	}

	/** Gets comment by product id. */
	//@Cacheable(value = "comments", key = "#productId")
	@Override
	public List<Comment> getCommentByProductId(Long productId) {
		return commentRepository.findByDeletedAtIsNullAndProductIdOrderByCreatedAtDesc(productId);
	}

	/** Creates comment. */
	//@CacheEvict(value = "comments", allEntries = true)
	@Transactional
	@Override
	public void createComment(Long userId, CommentCreateRequest commentCreateRequest) throws WrapperException {
		try {
			final Product product = productRepository.findByIdAndDeletedAtIsNull(commentCreateRequest.productId()).orElseThrow(
					() -> new NotFoundException("messages.products.notfound"));
			final User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("messages.users.notfound"));
			final Comment comment = commentMapper.createRequestToEntity(commentCreateRequest, user, product);
			commentRepository.save(comment);
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Updates comment. */
	//@CachePut(value = "comments")
	@Transactional
	@Override
	public void updateComment(Long id, Long userId, CommentUpdateRequest commentUpdateRequest) throws WrapperException {
		try {
			Comment comment = commentRepository.findByDeletedAtIsNullAndIdAndProductIdAndUserId(id,
			                                                                                    commentUpdateRequest.productId(),
			                                                                                    userId)
			                                   .orElseThrow(() -> new NotFoundException("messages.comments.notfound"));

			commentRepository.save(commentMapper.updateRequestToEntity(commentUpdateRequest, comment));
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Deletes comment. */
	//@CacheEvict(value = "comments", allEntries = true)
	@Transactional
	@Override
	public void deleteComment(Long id, Long userId) throws WrapperException {
		try {
			Comment comment = commentRepository.findByDeletedAtIsNullAndUserIdAndId(id, userId).orElseThrow(
					() -> new NotFoundException("messages.comments.notfound"));
			comment.setDeletedAt(TimeUtils.currentEpochMillis());
			commentRepository.save(comment);
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}
}


