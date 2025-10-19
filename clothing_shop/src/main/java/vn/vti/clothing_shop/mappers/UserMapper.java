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

    UserDTO entityToDTO(User user);

    List<UserDTO> listEntityToDTO(List<User> users);

    User createRequestToEntity(UserCreateRequest userCreateRequest, UserRole role);

    User updateRequestToEntity(UserUpdateRequest userUpdateRequest, @MappingTarget User user);

    UserLoginDTO entityToLoginDTO(User user, String token);

}
