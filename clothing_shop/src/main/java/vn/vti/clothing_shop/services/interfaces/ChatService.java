package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.ChatCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ChatReplyRequest;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateRequest;
import vn.vti.clothing_shop.entities.Chat;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface ChatService {
	List<Chat> getAllChat();

	List<Chat> getChat(Long userId);

	void addChat(Long userId, ChatCreateRequest chatCreateRequest) throws WrapperException;

	void updateChat(Long chatId, Long userId, ChatUpdateRequest chatUpdateRequest) throws WrapperException;

	void deleteChat(Long id, Long userId) throws WrapperException;

	void replyChat(Long chatId, Long userId, ChatReplyRequest chatReplyRequest) throws WrapperException;
}
