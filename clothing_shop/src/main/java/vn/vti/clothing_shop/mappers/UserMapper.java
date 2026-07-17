package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.dtos.ins.UserCreateRequest;
import vn.vti.clothing_shop.dtos.ins.UserUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.UserDTO;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.entities.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

	/** Maps to DTO. */
	UserDTO entityToDTO(User user);

	/** Handles list entity to DTO. */
	List<UserDTO> listEntityToDTO(List<User> users);

	/** Creates request to entity. */
	User createRequestToEntity(UserCreateRequest userCreateRequest, UserRole role);

	/** Updates request to entity. */
	User updateRequestToEntity(UserUpdateRequest userUpdateRequest, @MappingTarget User user);

	/** Maps to login DTO. */
	UserLoginDTO entityToLoginDTO(User user, String token);

}
