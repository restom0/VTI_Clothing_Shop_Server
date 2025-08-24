package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.ChatCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ChatReplyRequest;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.ChatDTO;
import vn.vti.clothing_shop.entities.Chat;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.ChatMapper;
import vn.vti.clothing_shop.repositories.ChatRepository;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.services.interfaces.ChatService;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    //@Cacheable(value = "chats")
    public List<ChatDTO> getAllChat() {
        return chatRepository.findAll().stream().map(chatMapper::entityToDTO).toList();
    }

    //@Cacheable(value = "chats", key = "#userId")
    public List<ChatDTO> getChat(Long userId) {
        return chatRepository.getByDeletedAtIsNullAndSenderId(userId).stream().map(chatMapper::entityToDTO).toList();
    }

    //@CacheEvict(value = "chats", allEntries = true)
    @Transactional
    public void addChat(Long userId, ChatCreateRequest chatCreateRequest) throws WrapperException {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
            Chat chat = chatMapper.createRequestToEntity(chatCreateRequest, user);
            chatRepository.save(chat);
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CachePut(value = "chats")
    @Transactional
    public void updateChat(Long chatId, Long userId, ChatUpdateRequest chatUpdateRequest) throws WrapperException {
        try {
            final Chat chat = chatRepository.findByDeletedAtIsNullAndIdAndSenderId(chatId, userId).orElseThrow(() -> new NotFoundException("Chat not found"));
            chatRepository.save(chatMapper.updateRequestToEntity(chatUpdateRequest, chat));
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CacheEvict(value = "chats", allEntries = true)
    @Transactional
    public void deleteChat(Long id, Long userId) throws WrapperException {
        try {
            Chat chat = chatRepository.findByDeletedAtIsNullAndIdAndSenderId(id, userId).orElseThrow(() -> new NotFoundException("Chat not found"));
            chat.setDeletedAt(Instant.now().toEpochMilli());
            chatRepository.save(chat);
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }

    //@CachePut(value = "chats")
    @Transactional
    public void replyChat(Long chatId, Long userId, ChatReplyRequest chatReplyRequest) throws WrapperException {
        try {
            User user = userRepository.findByDeletedAtIsNullAndId(userId).orElseThrow(() -> new NotFoundException("User not found"));
            Chat chat = chatRepository.findByDeletedAtIsNullAndId(chatId).orElseThrow(() -> new NotFoundException("Chat not found"));
            Chat replyChat = chatMapper.replyRequestToEntity(chatReplyRequest, user);
            chatRepository.save(replyChat);
            chat.setReply(replyChat);
            chatRepository.save(chat);
        } catch (NotFoundException ex) {
            throw new WrapperException(ex);
        }
    }
}


