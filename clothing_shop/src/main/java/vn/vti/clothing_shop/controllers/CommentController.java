package vn.vti.clothing_shop.controllers;

import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

import vn.vti.clothing_shop.dtos.ins.CommentCreateRequest;
import vn.vti.clothing_shop.dtos.ins.CommentUpdateRequest;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.CommentMapper;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.CommentService;

@AllArgsConstructor
@RestController
@RequestMapping("/comment")
public class CommentController {
	private final CommentService commentService;
	private final CommentMapper commentMapper;

	/** Gets all comment. */
	@Operation(summary = "Gets all comment", description = "Gets all comment API.")
	@GetMapping
	public ResponseEntity<BaseMessageResponse> getAllComment() {
		return ResponseHandler.successBuilder(HttpStatus.OK, commentService.getAllComments().stream()
		                                                                   .map(commentMapper::entityToDTO)
		                                                                   .toList());
	}

	/** Gets comment. */
	@Operation(summary = "Gets comment", description = "Gets comment API.")
	@GetMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> getComment(@PathVariable Long id) {
		return ResponseHandler.successBuilder(HttpStatus.OK, commentService.getCommentByProductId(id).stream()
		                                                                   .map(commentMapper::entityToDTO)
		                                                                   .toList());
	}

	/** Adds comment. */
	@Operation(summary = "Adds comment", description = "Adds comment API.")
	@PostMapping
	public ResponseEntity<BaseMessageResponse> addComment(@RequestBody @Valid CommentCreateRequest commentCreateRequest) {
		try {

			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) authentication.getPrincipal();
			Long userId = user.getId();
			commentService.createComment(userId, commentCreateRequest);
			return ResponseHandler.successBuilder(HttpStatus.CREATED, "messages.comments.created");
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Updates comment. */
	@Operation(summary = "Updates comment", description = "Updates comment API.")
	@PutMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> updateComment(
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id,
			@RequestBody @Valid CommentUpdateRequest commentUpdateRequest) {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) authentication.getPrincipal();
			Long userId = user.getId();
			commentService.updateComment(id, userId, commentUpdateRequest);
			return ResponseHandler.successBuilder(HttpStatus.CREATED, "messages.comments.created");
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}

	/** Deletes comment. */
	@Operation(summary = "Deletes comment", description = "Deletes comment API.")
	@DeleteMapping("/{id}")
	public ResponseEntity<BaseMessageResponse> deleteComment(
			@PathVariable @NotNull(message = "{messages.validation.required}") Long id) {

		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User user = (User) authentication.getPrincipal();
			Long userId = user.getId();
			commentService.deleteComment(id, userId);
			return ResponseHandler.successBuilder(HttpStatus.OK, "messages.comments.deleted");
		} catch (WrapperException e) {
			return ResponseHandler.exceptionBuilder(e);
		}
	}
}
