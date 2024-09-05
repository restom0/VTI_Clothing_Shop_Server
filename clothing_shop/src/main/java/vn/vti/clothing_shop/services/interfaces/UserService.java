package vn.vti.clothing_shop.services.interfaces;

import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.UserCreateDTO;
import vn.vti.clothing_shop.dtos.ins.UserReadDTO;
import vn.vti.clothing_shop.dtos.ins.UserUpdateDTO;
import vn.vti.clothing_shop.dtos.ins.UserUpdatePasswordDTO;
import vn.vti.clothing_shop.dtos.outs.UserDTO;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;

import java.util.List;

@Component
public interface UserService {
    List<UserDTO> getAllUsers();
    UserLoginDTO getUser(UserReadDTO userReadDTO);
    Long countUser();
    UserDTO getUserById(Long id);
    Boolean addUser(UserCreateDTO userCreateDTO);
    Boolean updateUser(UserUpdateDTO userUpdateDTO);
    Boolean updateUserPassword(UserUpdatePasswordDTO userUpdatePasswordDTO);
    Boolean deleteUser(Long id);
}
