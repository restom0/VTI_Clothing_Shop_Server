package vn.vti.clothing_shop.controllers;

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
import vn.vti.clothing_shop.dtos.ins.UserCreateRequest;
import vn.vti.clothing_shop.dtos.ins.UserLoginRequest;
import vn.vti.clothing_shop.dtos.ins.UserUpdatePasswordRequest;
import vn.vti.clothing_shop.dtos.ins.UserUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.UserDTO;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.services.interfaces.UserService;

@AllArgsConstructor
@RequestMapping(value = "/user")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<BaseMessageResponse> loginUser(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        try {
            UserLoginDTO user = userService.getUser(userLoginRequest);
            return ResponseHandler.successBuilder(HttpStatus.OK, user);
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<BaseMessageResponse> registerUser(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        try {
            userService.addUser(userCreateRequest);
            return ResponseHandler.successBuilder(HttpStatus.CREATED, "Đăng ký thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<BaseMessageResponse> getUserById() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            UserDTO userDTO = userService.getUserById(userId);
            return ResponseHandler.successBuilder(HttpStatus.OK, userDTO);
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/")
    public ResponseEntity<BaseMessageResponse> getAllUsers() {
        return ResponseHandler.successBuilder(HttpStatus.OK, userService.getUsers());
    }

    @PutMapping("/")
    public ResponseEntity<BaseMessageResponse> updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            userService.updateUser(userUpdateRequest, userId);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Cập nhật thông tin người dùng thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/password")
    public ResponseEntity<BaseMessageResponse> updatePassword(@RequestBody @Valid UserUpdatePasswordRequest userUpdateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            userService.updateUserPassword(userUpdateRequest, userId);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Cập nhật mật khẩu thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseMessageResponse> deleteUser(@PathVariable @NotNull(message = "Vui lòng chọn người dùng") Long id) {
        try {
            userService.deleteUser(id);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Xóa người dùng thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<BaseMessageResponse> deleteAccount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            userService.deleteUser(userId);
            return ResponseHandler.successBuilder(HttpStatus.OK, "Xóa người dùng thành công");
        } catch (WrapperException e) {
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
