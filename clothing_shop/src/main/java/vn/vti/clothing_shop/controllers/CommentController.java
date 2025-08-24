package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vti.clothing_shop.dtos.ins.CommentCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CommentUpdateRequest;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.CommentService;

@AllArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getAllComment() {
        return ResponseHandler.successBuilder(HttpStatus.OK, commentService.getAllComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> getComment(@PathVariable Long id) {
        return ResponseHandler.successBuilder(HttpStatus.OK, commentService.getCommentByProductId(id));
    }

    @PostMapping("/")
    public ResponseEntity<BaseMessageResponse> addComment(@RequestBody @Valid CommentCreateRequest commentCreateRequest) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            commentService.createComment(userId, commentCreateRequest);
            return ResponseHandler.successBuilder(HttpStatus.CREATED, "Bình luận đã được gửi");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> updateComment(@PathVariable @NotNull(message = "Hãy chọn bình luận") Long id, @RequestBody @Valid CommentUpdateRequest commentUpdateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            commentService.updateComment(id, userId, commentUpdateRequest);
            return ResponseHandler.successBuilder(HttpStatus.CREATED, "Bình luận đã được gửi");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> deleteComment(@PathVariable @NotNull(message = "Hãy chọn bình luận") Long id, BindingResult bindingResult) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            commentService.deleteComment(id, userId);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Bình luận đã được xóa");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
