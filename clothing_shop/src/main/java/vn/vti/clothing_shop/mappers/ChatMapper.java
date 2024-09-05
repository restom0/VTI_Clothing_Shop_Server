package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.ChatCreateDTO;
import vn.vti.clothing_shop.dtos.ins.ChatReplyDTO;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.ChatDTO;
import vn.vti.clothing_shop.dtos.outs.UserDTO;
import vn.vti.clothing_shop.entities.Chat;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.requests.ChatCreateRequest;
import vn.vti.clothing_shop.requests.ChatReplyRequest;
import vn.vti.clothing_shop.requests.ChatUpdateRequest;

@Component
public class ChatMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public ChatMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ChatDTO EntityToDTO(Chat chat) {
        ChatDTO chatDTO = modelMapper.map(chat, ChatDTO.class);
        chatDTO.setSender_id(modelMapper.map(chat.getSender_id(), UserDTO.class));
        return chatDTO;
    }

    public ChatCreateDTO ChatCreateRequestToChatCreateDTO(ChatCreateRequest chatCreateRequest) {
        return modelMapper.map(chatCreateRequest, ChatCreateDTO.class);
    }

    public ChatReplyDTO ChatReplyRequestToChatReplyDTO(ChatReplyRequest chatReplyRequest) {
        return modelMapper.map(chatReplyRequest, ChatReplyDTO.class);
    }

    public Chat ChatCreateDTOToChat(ChatCreateDTO chatCreateDTO, User user) {
        Chat chat = modelMapper.map(chatCreateDTO, Chat.class);
        chat.setSender_id(user);
        return chat;
    }

    public ChatUpdateDTO ChatUpdateRequestToChatUpdateDTO(ChatUpdateRequest chatUpdateRequest) {
        return modelMapper.map(chatUpdateRequest, ChatUpdateDTO.class);
    }

    public Chat ChatUpdateDTOToChat(ChatUpdateDTO chatUpdateDTO,Chat chat) {
        chat.setContent(chatUpdateDTO.getContent());
        return chat;
    }

    public ChatReplyDTO ChatReplyRequestToChatReplyDto(ChatReplyDTO chatReplyDTO) {
        return modelMapper.map(chatReplyDTO, ChatReplyDTO.class);
    }
    public Chat ChatReplyDTOToChat(ChatReplyDTO chatReplyDTO,Chat chat) {
        chat.setReply(chatReplyDTO.getReply());
        return chat;
    }
}
