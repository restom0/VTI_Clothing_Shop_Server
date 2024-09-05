package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import vn.vti.clothing_shop.mappers.ChatMapper;
import vn.vti.clothing_shop.requests.ChatCreateRequest;
import vn.vti.clothing_shop.requests.ChatReplyRequest;
import vn.vti.clothing_shop.requests.ChatUpdateRequest;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.InternalServerErrorException;
import vn.vti.clothing_shop.services.implementations.ChatServiceImplementation;
import vn.vti.clothing_shop.utils.ParameterUtils;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatServiceImplementation chatServiceImplementation;
    private final ChatMapper chatMapper;

    @Autowired
    public ChatController(ChatServiceImplementation chatServiceImplementation, ChatMapper chatMapper) {
        this.chatServiceImplementation = chatServiceImplementation;
        this.chatMapper = chatMapper;
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllChats(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            return ResponseHandler.responseBuilder(200, "Lấy tin nhắn thành công", chatServiceImplementation.getAllChat(), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @GetMapping("/")
    public ResponseEntity<?> getChat(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            return ResponseHandler.responseBuilder(200, "Lấy tin nhắn thành công", chatServiceImplementation.getChat(userId), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @PostMapping("/")
    public ResponseEntity<?> addChat(@RequestBody @Valid ChatCreateRequest chatCreateRequest, BindingResult bindingResult){

        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            if(chatServiceImplementation.addChat(userId,chatMapper.ChatCreateRequestToChatCreateDTO(chatCreateRequest))){
                return ResponseHandler.responseBuilder(201,"Tin nhắn được gửi thành công",null,HttpStatus.CREATED);
            }
            throw new InternalServerErrorException("Gửi tin nhắn thất bại");
        }
        catch(Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateChat(
            @RequestBody
            @Valid
            ChatUpdateRequest chatUpdateRequest,
            @PathVariable
            @NotNull(message = "Vui lòng chọn tin nhắn")
            Long id,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            return ParameterUtils.showBindingResult(bindingResult);
        }
        try{
            if(chatServiceImplementation.updateChat(id,chatMapper.ChatUpdateRequestToChatUpdateDTO(chatUpdateRequest))){
                return ResponseHandler.responseBuilder(200,"Tin nhắn được cập nhật thành công",null,HttpStatus.OK);
            }
            throw new InternalServerErrorException("Cập nhật tin nhắn thất bại");
        }
        catch(Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChat(
            @PathVariable
            @NotNull(message = "Vui lòng chọn tin nhắn")
            @Pattern(regexp = "^[0-9]*$", message = "Id tin nhắn không hợp lệ")
            Long id
    ){
        try{
            if(chatServiceImplementation.deleteChat(id)){
                return ResponseHandler.responseBuilder(200,"Xóa tin nhắn thành công",null,HttpStatus.OK);
            }
            throw new InternalServerErrorException("Xóa tin nhắn thất bại");
        }
        catch(Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @PutMapping("/reply/{id}")
    public ResponseEntity<?> replyChat(
            @RequestBody
            @Valid
            ChatReplyRequest chatReplyRequest,
            @PathVariable
            @NotNull(message = "Vui lòng chọn tin nhắn")
            Long id,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            return ParameterUtils.showBindingResult(bindingResult);
        }
        try{
            if(chatServiceImplementation.replyChat(id,chatMapper.ChatReplyRequestToChatReplyDTO(chatReplyRequest))){
                return ResponseHandler.responseBuilder(200,"Trả lời tin nhắn thành công",null,HttpStatus.OK);
            }
            throw new InternalServerErrorException("Trả lời tin nhắn thất bại");
        }
        catch(Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
