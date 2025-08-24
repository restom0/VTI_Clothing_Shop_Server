package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.CommentCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CommentUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.CommentDTO;
import vn.vti.clothing_shop.entities.Comment;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.entities.User;

@Mapper
public interface CommentMapper {
    CommentDTO entityToDTO(Comment comment);

    Comment createRequestToEntity(CommentCreateRequest commentCreateRequest, User user, Product product);

    Comment updateRequestToEntity(CommentUpdateRequest commentUpdateRequest, @MappingTarget Comment comment);

}