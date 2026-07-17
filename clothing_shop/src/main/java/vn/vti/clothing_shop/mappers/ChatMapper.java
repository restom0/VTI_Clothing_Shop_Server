package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import vn.vti.clothing_shop.dtos.ins.ChatCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ChatReplyRequest;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ChatDTO;
import vn.vti.clothing_shop.entities.Chat;
import vn.vti.clothing_shop.entities.User;

@Mapper(componentModel = "spring")
public interface ChatMapper {
	/** Maps to DTO. */
	@Mapping(target = "reply", source = "reply.content")
	ChatDTO entityToDTO(Chat chat);

	/** Creates request to entity. */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "sender", source = "senderId")
	@Mapping(target = "content", source = "chatCreateRequest.content")
	@Mapping(target = "reply", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	Chat createRequestToEntity(ChatCreateRequest chatCreateRequest, User senderId);

	/** Replies to request to entity. */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "sender", source = "senderId")
	@Mapping(target = "content", source = "chatReplyRequest.reply")
	@Mapping(target = "reply", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	@Mapping(target = "version", ignore = true)
	Chat replyRequestToEntity(ChatReplyRequest chatReplyRequest, User senderId);

	/** Updates request to entity. */
	@Mapping(target = "reply", ignore = true)
	@Mapping(target = "sender", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "deletedAt", ignore = true)
	Chat updateRequestToEntity(ChatUpdateRequest chatUpdateRequest, @MappingTarget Chat chat);
}
