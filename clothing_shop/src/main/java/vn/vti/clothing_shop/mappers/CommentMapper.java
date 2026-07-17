package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.vti.clothing_shop.dtos.ins.CommentCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CommentUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.CommentDTO;
import vn.vti.clothing_shop.entities.Comment;
import vn.vti.clothing_shop.entities.Product;
import vn.vti.clothing_shop.entities.User;

@Mapper(componentModel = "spring", uses = { DateMapper.class, ProductMapper.class, UserMapper.class })
public interface CommentMapper {
	/** Maps to DTO. */
	@Mapping(target = "userId", source = "user")
	@Mapping(target = "productId", source = "product")
	CommentDTO entityToDTO(Comment comment);

	/** Creates request to entity. */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", source = "user")
	@Mapping(target = "product", source = "product")
	@Mapping(target = "status", constant = "true")
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	Comment createRequestToEntity(CommentCreateRequest commentCreateRequest, User user, Product product);

	/** Updates request to entity. */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "product", ignore = true)
	@Mapping(target = "status", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	Comment updateRequestToEntity(CommentUpdateRequest commentUpdateRequest, @MappingTarget Comment comment);

}
