package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.CommentCreateDTO;
import vn.vti.clothing_shop.dtos.ins.CommentUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.CommentDTO;
import vn.vti.clothing_shop.entities.Comment;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.requests.CommentCreateRequest;
import vn.vti.clothing_shop.requests.CommentUpdateRequest;

import static vn.vti.clothing_shop.constants.StarMin.STAR_MIN;

@Component
public class CommentMapper {
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    @Autowired
    public CommentMapper(ModelMapper modelMapper, UserMapper userMapper, ProductMapper productMapper) {
        this.modelMapper = modelMapper;
        this.userMapper = userMapper;
        this.productMapper = productMapper;
    }

    public CommentDTO EntityToDTO(Comment comment) {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        commentDTO.setUser_id(userMapper.EntityToDTO(comment.getUser_id()));
        commentDTO.setProduct_id(productMapper.EntityToDTO(comment.getProduct_id()));
        return commentDTO;
    }

     public CommentCreateDTO CommentCreateRequestToCommentCreateDTO(CommentCreateRequest commentCreateRequest){
        return modelMapper.map(commentCreateRequest, CommentCreateDTO.class);
     }

     public CommentUpdateDTO CommentUpdateRequestToCommentUpdateDTO(CommentUpdateRequest commentUpdateRequest){
        return modelMapper.map(commentUpdateRequest, CommentUpdateDTO.class);
     }

     public Comment CommentCreateDTOToEntity(CommentCreateDTO commentCreateDTO, User user, Product product){
        Comment comment= modelMapper.map(commentCreateDTO, Comment.class);
        comment.setProduct_id(product);
        comment.setUser_id(user);
        comment.setStatus(commentCreateDTO.getStar() >= STAR_MIN);
        return comment;
    }

    public Comment CommentUpdateDTOToEntity(CommentUpdateDTO commentUpdateDTO,Comment comment){
        comment.setContent(commentUpdateDTO.getContent());
        comment.setStar(commentUpdateDTO.getStar());
        comment.setStatus(commentUpdateDTO.getStar() >= STAR_MIN);
        return comment;
    }
}
