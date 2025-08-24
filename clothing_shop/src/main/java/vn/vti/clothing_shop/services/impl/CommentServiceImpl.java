package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.CommentCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CommentUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.CommentDTO;
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

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;


    //@Cacheable(value = "comments")
    public List<CommentDTO> getAllComments() {
        return commentRepository.findAll().stream()
                .map(commentMapper::entityToDTO)
                .toList();
    }

    //@Cacheable(value = "comments", key = "#productId")
    public List<CommentDTO> getCommentByProductId(Long productId) {
        return commentRepository.findByDeletedAtIsNullAndProductIdOrderByCreatedAtDesc(productId).stream()
                .map(commentMapper::entityToDTO)
                .toList();
    }

    //@CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void createComment(Long userId, CommentCreateRequest commentCreateRequest) throws WrapperException {
        try {
            final Product product = productRepository.findByIdAndDeletedAtIsNull(commentCreateRequest.productId()).orElseThrow(() -> new NotFoundException("Product not found"));
            final User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
            final Comment comment = commentMapper.createRequestToEntity(commentCreateRequest, user, product);
            commentRepository.save(comment);
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CachePut(value = "comments")
    @Transactional
    public void updateComment(Long id, Long userId, CommentUpdateRequest commentUpdateRequest) throws WrapperException {
        try {
            Comment comment = commentRepository.findByDeletedAtIsNullAndIdAndProductIdAndUserId(id, commentUpdateRequest.productId(), userId)
                    .orElseThrow(() -> new NotFoundException("Comment not found"));

            commentRepository.save(commentMapper.updateRequestToEntity(commentUpdateRequest, comment));
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CacheEvict(value = "comments", allEntries = true)
    @Transactional
    public void deleteComment(Long id, Long userId) throws WrapperException {
        try {
            Comment comment = commentRepository.findByDeletedAtIsNullAndUserIdAndId(id, userId).orElseThrow(() -> new NotFoundException("Comment not found"));
            comment.setDeletedAt(Instant.now().toEpochMilli());
            commentRepository.save(comment);
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }
}


