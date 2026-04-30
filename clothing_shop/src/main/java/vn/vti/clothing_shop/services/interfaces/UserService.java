package vn.vti.clothing_shop.services.interfaces;

import vn.vti.clothing_shop.dtos.ins.UserCreateRequest;
import vn.vti.clothing_shop.dtos.ins.UserLoginRequest;
import vn.vti.clothing_shop.dtos.ins.UserUpdatePasswordRequest;
import vn.vti.clothing_shop.dtos.ins.UserUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.WrapperException;

import java.util.List;

public interface UserService {
	List<User> getUsers();

	UserLoginDTO getUser(UserLoginRequest userLoginRequest) throws WrapperException;

	Long countUser();

	User getUserById(Long id) throws WrapperException;

	void addUser(UserCreateRequest userCreateRequest) throws WrapperException;

	void updateUser(UserUpdateRequest userUpdateRequest, Long id) throws WrapperException;

	void updateUserPassword(UserUpdatePasswordRequest userUpdatePasswordRequest, Long id) throws WrapperException;

	void deleteUser(Long id) throws WrapperException;
}
