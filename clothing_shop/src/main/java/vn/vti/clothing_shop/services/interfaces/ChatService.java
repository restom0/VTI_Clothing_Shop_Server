package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.ChatCreateDTO;
import vn.vti.clothing_shop.dtos.ins.ChatReplyDTO;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateDTO;
import vn.vti.clothing_shop.dtos.outs.ChatDTO;

import java.util.List;

public interface ChatService {
    List<ChatDTO> getAllChat();
    List<ChatDTO> getChat(Long sender_id);
    Boolean addChat(Long user_id, ChatCreateDTO chatCreateDTO);
    Boolean updateChat(Long chat_id, ChatUpdateDTO chatUpdateDTO);
    Boolean deleteChat(Long id);
    Boolean replyChat(Long id, ChatReplyDTO chatReplyDTO);
}
