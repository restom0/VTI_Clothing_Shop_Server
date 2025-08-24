package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.ChatCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ChatReplyRequest;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ChatDTO;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface ChatService {
    List<ChatDTO> getAllChat();

    List<ChatDTO> getChat(Long userId);

    void addChat(Long userId, ChatCreateRequest chatCreateRequest) throws WrapperException;

    void updateChat(Long chatId, Long userId, ChatUpdateRequest chatUpdateRequest) throws WrapperException;

    void deleteChat(Long id, Long userId) throws WrapperException;

    void replyChat(Long chatId, Long userId, ChatReplyRequest chatReplyRequest) throws WrapperException;
}
