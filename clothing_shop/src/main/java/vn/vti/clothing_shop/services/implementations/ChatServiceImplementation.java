package vn.vti.clothing_shop.services.implementations;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import vn.vti.clothing_shop.dtos.ins.ChatCreateDTO;
import vn.vti.clothing_shop.dtos.ins.ChatReplyDTO;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.ChatDTO;
import vn.vti.clothing_shop.entities.Chat;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.mappers.ChatMapper;
import vn.vti.clothing_shop.repositories.ChatRepository;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.services.interfaces.ChatService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImplementation implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    @Autowired
    public ChatServiceImplementation(ChatRepository chatRepository, UserRepository userRepository, ChatMapper chatMapper) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.chatMapper = chatMapper;
    }

    //@Cacheable(value = "chats")
    public List<ChatDTO> getAllChat(){
        return this.chatRepository.findAll().stream().map(chatMapper::EntityToDTO).collect(Collectors.toList());
    };

    //@Cacheable(value = "chats", key = "#sender_id")
    public List<ChatDTO> getChat(Long sender_id){
        return chatRepository.findBySenderId(sender_id).stream().map(chatMapper::EntityToDTO).collect(Collectors.toList());
    };

    //@CacheEvict(value = "chats", allEntries = true)
    @Transactional
    public Boolean addChat(Long user_id, ChatCreateDTO chatCreateDTO){
        User user = userRepository.findById(user_id).orElseThrow(()-> new NotFoundException("User not found"));
        chatRepository.save(chatMapper.ChatCreateDTOToChat(chatCreateDTO, user));
        return true;
    }

    //@CachePut(value = "chats")
    @Transactional
    public Boolean updateChat(Long chat_id, ChatUpdateDTO chatUpdateDTO){
        Chat chat = chatRepository.findById(chat_id).orElseThrow(()-> new NotFoundException("Chat not found"));
        chatRepository.save(chatMapper.ChatUpdateDTOToChat(chatUpdateDTO, chat));
        return true;
    };

    //@CacheEvict(value = "chats", allEntries = true)
    @Transactional
    public Boolean deleteChat(Long id){
        Chat chat = chatRepository.findById(id).orElseThrow(()->new NotFoundException("Chat not found"));
        chat.setDeleted_at(LocalDateTime.now());
        return true;
    };

    //@CachePut(value = "chats")
    @Transactional
    public Boolean replyChat(Long id, ChatReplyDTO chatReplyDTO) {
        Chat chat = chatRepository.findById(id).orElseThrow(()-> new NotFoundException("Chat not found"));
        chatRepository.save(chatMapper.ChatReplyDTOToChat(chatReplyDTO,chat));
        return true;
    }
}


