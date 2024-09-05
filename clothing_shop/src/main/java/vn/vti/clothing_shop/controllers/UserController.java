package vn.vti.clothing_shop.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.mappers.UserMapper;
import vn.vti.clothing_shop.requests.UserCreateRequest;
import vn.vti.clothing_shop.requests.UserReadRequest;
import vn.vti.clothing_shop.requests.UserUpdatePasswordRequest;
import vn.vti.clothing_shop.requests.UserUpdateRequest;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.exceptions.InternalServerErrorException;
import vn.vti.clothing_shop.services.implementations.UserServiceImplementation;
import vn.vti.clothing_shop.utils.ParameterUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
@RequestMapping(value = "/user")
@RestController
public class UserController {

    private final UserServiceImplementation userServiceImplementation;
    private final UserMapper userMapper;

    @Autowired
    public UserController(UserServiceImplementation userServiceImplementation, UserMapper userMapper) {
        this.userServiceImplementation = userServiceImplementation;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserReadRequest userReadRequest, BindingResult bindingResult){
        try {
            if(bindingResult.hasErrors()){
                return ParameterUtils.showBindingResult(bindingResult);
            }
            return  ResponseHandler.responseBuilder(200,"Đăng nhập thành công", userServiceImplementation.getUser(userMapper.ReadRequestToReadDTO(userReadRequest)), HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserCreateRequest userCreateRequest, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            if(userServiceImplementation.addUser(userMapper.CreateRequestToCreateDTO(userCreateRequest)))
                return ResponseHandler.responseBuilder(201,"Đăng ký thành công",null, HttpStatus.CREATED);
            throw new InternalServerErrorException("Đăng ký thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @GetMapping("/profile")
    public ResponseEntity<?> getUserById(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            return ResponseHandler.responseBuilder(200,"Lấy thông tin người dùng thành công",userServiceImplementation.getUserById(userId),HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers(){
        try {
            return ResponseHandler.responseBuilder(200,"Lấy danh sách người dùng thành công",userServiceImplementation.getAllUsers(),HttpStatus.OK);
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }

    @PutMapping("/")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            if(userServiceImplementation.updateUser(userMapper.UpdateRequestToUpdateDTO(userUpdateRequest,userId)))
                return ResponseHandler.responseBuilder(200,"Cập nhật thông tin người dùng thành công",null,HttpStatus.OK);
            throw new InternalServerErrorException("Cập nhật thông tin người dùng thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UserUpdatePasswordRequest userUpdateRequest, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors())
                return ParameterUtils.showBindingResult(bindingResult);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            if(userServiceImplementation.updateUserPassword(userMapper.UpdatePasswordRequestToUpdatePasswordDTO(userUpdateRequest,userId)))
                return ResponseHandler.responseBuilder(200,"Cập nhật mật khẩu thành công",null,HttpStatus.OK);
            throw new InternalServerErrorException("Cập nhật mật khẩu thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable @NotNull(message = "Vui lòng chọn người dùng") Long id){
        try {
            if(userServiceImplementation.deleteUser(id))
                return ResponseHandler.responseBuilder(200,"Xóa người dùng thành công",null,HttpStatus.OK);
            throw new InternalServerErrorException("Xóa người dùng thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
    @DeleteMapping("/")
    public ResponseEntity<?> deleteAccount(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();
            Long userId = user.getId();
            if(userServiceImplementation.deleteUser(userId))
                return ResponseHandler.responseBuilder(200,"Xóa người dùng thành công",null,HttpStatus.OK);
            throw new InternalServerErrorException("Xóa người dùng thất bại");
        }
        catch (Exception e){
            return ResponseHandler.exceptionBuilder(e);
        }
    }
}
