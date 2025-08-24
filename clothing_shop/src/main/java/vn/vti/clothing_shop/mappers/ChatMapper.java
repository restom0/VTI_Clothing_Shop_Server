package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import vn.vti.clothing_shop.dtos.ins.ChatCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ChatReplyRequest;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ChatDTO;
import vn.vti.clothing_shop.entities.Chat;
import vn.vti.clothing_shop.entities.User;

@Mapper
public interface ChatMapper {
    ChatDTO entityToDTO(Chat chat);

    Chat createRequestToEntity(ChatCreateRequest chatCreateRequest, User senderId);

    Chat replyRequestToEntity(ChatReplyRequest chatReplyRequest, User senderId);

    Chat updateRequestToEntity(ChatUpdateRequest chatUpdateRequest, @MappingTarget Chat chat);
}