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
	/** Gets users. */
	List<User> getUsers();

	/** Gets user. */
	UserLoginDTO getUser(UserLoginRequest userLoginRequest) throws WrapperException;

	/** Counts user. */
	Long countUser();

	/** Gets user by id. */
	User getUserById(Long id) throws WrapperException;

	/** Adds user. */
	void addUser(UserCreateRequest userCreateRequest) throws WrapperException;

	/** Updates user. */
	void updateUser(UserUpdateRequest userUpdateRequest, Long id) throws WrapperException;

	/** Updates user password. */
	void updateUserPassword(UserUpdatePasswordRequest userUpdatePasswordRequest, Long id) throws WrapperException;

	/** Deletes user. */
	void deleteUser(Long id) throws WrapperException;
}
