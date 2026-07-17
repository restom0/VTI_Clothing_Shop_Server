package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.constants.Messages;
import vn.vti.clothing_shop.dtos.ins.ChatCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ChatReplyRequest;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateRequest;
import vn.vti.clothing_shop.entities.Chat;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.ChatMapper;
import vn.vti.clothing_shop.repositories.ChatRepository;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.services.interfaces.ChatService;
import vn.vti.clothing_shop.utils.TimeUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
	private final ChatRepository chatRepository;
	private final UserRepository userRepository;
	private final ChatMapper chatMapper;

	/** Gets all chat. */
	//@Cacheable(value = "chats")
	@Override
	public List<Chat> getAllChat() {
		return chatRepository.findAll();
	}

	/** Gets chat. */
	//@Cacheable(value = "chats", key = "#userId")
	@Override
	public List<Chat> getChat(Long userId) {
		return chatRepository.getByDeletedAtIsNullAndSenderId(userId);
	}

	/** Adds chat. */
	//@CacheEvict(value = "chats", allEntries = true)
	@Transactional
	@Override
	public void addChat(Long userId, ChatCreateRequest chatCreateRequest) throws WrapperException {
		try {
			User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("messages.users.notfound"));
			Chat chat = chatMapper.createRequestToEntity(chatCreateRequest, user);
			chatRepository.save(chat);
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Updates chat. */
	//@CachePut(value = "chats")
	@Transactional
	@Override
	public void updateChat(Long chatId, Long userId, ChatUpdateRequest chatUpdateRequest) throws WrapperException {
		try {
			final Chat chat = chatRepository.findByDeletedAtIsNullAndIdAndSenderId(chatId, userId).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_CHAT_NOTFOUND));
			chatRepository.save(chatMapper.updateRequestToEntity(chatUpdateRequest, chat));
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Deletes chat. */
	//@CacheEvict(value = "chats", allEntries = true)
	@Transactional
	@Override
	public void deleteChat(Long id, Long userId) throws WrapperException {
		try {
			Chat chat = chatRepository.findByDeletedAtIsNullAndIdAndSenderId(id, userId).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_CHAT_NOTFOUND));
			chat.setDeletedAt(TimeUtils.currentEpochMillis());
			chatRepository.save(chat);
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}

	/** Replies to chat. */
	//@CachePut(value = "chats")
	@Transactional
	@Override
	public void replyChat(Long chatId, Long userId, ChatReplyRequest chatReplyRequest) throws WrapperException {
		try {
			User user = userRepository.findByDeletedAtIsNullAndId(userId).orElseThrow(
					() -> new NotFoundException("messages.users.notfound"));
			Chat chat = chatRepository.findByDeletedAtIsNullAndId(chatId).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_CHAT_NOTFOUND));
			Chat replyChat = chatMapper.replyRequestToEntity(chatReplyRequest, user);
			chatRepository.save(replyChat);
			chat.setReply(replyChat);
			chatRepository.save(chat);
		} catch (NotFoundException ex) {
			throw new WrapperException(ex);
		}
	}
}


