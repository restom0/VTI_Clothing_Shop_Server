package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.dtos.ins.ChatCreateRequest;
import vn.vti.clothing_shop.dtos.ins.ChatReplyRequest;
import vn.vti.clothing_shop.dtos.ins.ChatUpdateRequest;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.ChatService;

@AllArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/all")
    public ResponseEntity<BaseMessageResponse> getAllChats() {
        return ResponseHandler.successBuilder(HttpStatus.OK, chatService.getAllChat());
    }

    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getChat() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return ResponseHandler.successBuilder(HttpStatus.OK, chatService.getChat(userId));
    }

    @PostMapping("/")
    public ResponseEntity<BaseMessageResponse> addChat(@RequestBody @Valid ChatCreateRequest chatCreateRequest) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            chatService.addChat(userId, chatCreateRequest);
            return ResponseHandler.successBuilder(HttpStatus.CREATED, "Tin nhắn được gửi thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> updateChat(
            @RequestBody
            @Valid
            ChatUpdateRequest chatUpdateRequest,
            @PathVariable
            @NotNull(message = "Vui lòng chọn tin nhắn")
            Long id
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            chatService.updateChat(id, userId, chatUpdateRequest);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Tin nhắn được cập nhật thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> deleteChat(
            @PathVariable
            @NotNull(message = "Vui lòng chọn tin nhắn")
            @Pattern(regexp = "^\\d+$", message = "Id tin nhắn không hợp lệ")
            Long id
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            chatService.deleteChat(id, userId);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Xóa tin nhắn thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/reply/{id}")
    public ResponseEntity<BaseMessageResponse> replyChat(
            @RequestBody
            @Valid
            ChatReplyRequest chatReplyRequest,
            @PathVariable
            @NotNull(message = "Vui lòng chọn tin nhắn")
            Long id
    ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            chatService.replyChat(id, userId, chatReplyRequest);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Trả lời tin nhắn thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
