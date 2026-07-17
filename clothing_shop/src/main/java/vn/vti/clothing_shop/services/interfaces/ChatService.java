package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.ChatCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ChatReplyRequest;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateRequest;
import vn.vti.clothing_shop.entities.Chat;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface ChatService {
	/** Gets all chat. */
	List<Chat> getAllChat();

	/** Gets chat. */
	List<Chat> getChat(Long userId);

	/** Adds chat. */
	void addChat(Long userId, ChatCreateRequest chatCreateRequest) throws WrapperException;

	/** Updates chat. */
	void updateChat(Long chatId, Long userId, ChatUpdateRequest chatUpdateRequest) throws WrapperException;

	/** Deletes chat. */
	void deleteChat(Long id, Long userId) throws WrapperException;

	/** Replies to chat. */
	void replyChat(Long chatId, Long userId, ChatReplyRequest chatReplyRequest) throws WrapperException;
}
