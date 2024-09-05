package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.CommentCreateDTO;
import vn.vti.clothing_shop.dtos.ins.CommentUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.CommentDTO;

import java.util.List;

public interface CommentService {
    List<CommentDTO> getAllComments();
    List<CommentDTO> getCommentById(Long product_id);
    Boolean createComment(Long user_id, CommentCreateDTO commentCreateDTO);
    Boolean updateComment(Long id, Long user_id, CommentUpdateDTO commentUpdateDTO);
    Boolean deleteComment(Long id);
}
