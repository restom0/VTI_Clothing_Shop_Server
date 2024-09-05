package vn.vti.clothing_shop.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.dtos.ins.UserCreateDTO;
import vn.vti.clothing_shop.dtos.ins.UserReadDTO;
import vn.vti.clothing_shop.dtos.ins.UserUpdateDTO;
import vn.vti.clothing_shop.dtos.ins.UserUpdatePasswordDTO;
import vn.vti.clothing_shop.dtos.outs.UserDTO;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.requests.UserCreateRequest;
import vn.vti.clothing_shop.requests.UserReadRequest;
import vn.vti.clothing_shop.requests.UserUpdatePasswordRequest;
import vn.vti.clothing_shop.requests.UserUpdateRequest;
import vn.vti.clothing_shop.services.JwtService;

import java.util.List;

@Component
public class UserMapper {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public UserMapper(ModelMapper modelMapper, PasswordEncoder passwordEncoder, JwtService jwtService, RedisTemplate<String, Object> redisTemplate) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    public UserDTO EntityToDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        //redisTemplate.opsForHash().put("user", userDTO.getId(), userDTO);
        return userDTO;
    }

    public List<UserDTO> EntityToDTO(List<User> users) {
        return users.stream().map(this::EntityToDTO).toList();
    }

    public UserCreateDTO CreateRequestToCreateDTO(UserCreateRequest userCreateRequest) {
        return modelMapper.map(userCreateRequest, UserCreateDTO.class);
    }

    private User CreateDTOToEntity(UserCreateDTO userCreateDTO, UserRole userRole) {
        User user = modelMapper.map(userCreateDTO, User.class);
        String salt = jwtService.generateSalt(userCreateDTO.getUsername() + userCreateDTO.getEmail() + userCreateDTO.getPhone_number());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword() + salt));
        user.setSalt(salt);
        user.setRole(userRole);
        return user;
    }

    public User createNormalUser(UserCreateDTO userCreateDTO) {
        return CreateDTOToEntity(userCreateDTO, UserRole.USER);
    }

    public User createAdminUser(UserCreateDTO userCreateDTO) {
        return CreateDTOToEntity(userCreateDTO, UserRole.ADMIN);
    }

    public UserUpdateDTO UpdateRequestToUpdateDTO(UserUpdateRequest userUpdateRequest, Long id) {
        UserUpdateDTO userUpdateDTO = modelMapper.map(userUpdateRequest, UserUpdateDTO.class);
        userUpdateDTO.setId(id);
        return userUpdateDTO;
    }

    public User UpdateDTOToEntity(User user, UserUpdateDTO userUpdateDTO) {
        user.setName(userUpdateDTO.getName());
        user.setBirthday(userUpdateDTO.getBirthday());
        user.setAvatar_url(userUpdateDTO.getAvatar_url());
        user.setAddress(userUpdateDTO.getAddress());
        user.setGender(userUpdateDTO.getGender());
        user.setPublic_id_avatar_url(userUpdateDTO.getPublic_id_avatar_url());
        redisTemplate.opsForHash().put("USER", user.getId().toString(), user);
        return user;
    }

    public UserUpdatePasswordDTO UpdatePasswordRequestToUpdatePasswordDTO(UserUpdatePasswordRequest userUpdatePasswordRequest, Long id) {
        UserUpdatePasswordDTO userUpdatePasswordDTO = modelMapper.map(userUpdatePasswordRequest, UserUpdatePasswordDTO.class);
        userUpdatePasswordDTO.setId(id);
        return userUpdatePasswordDTO;
    }

    public User UpdatePasswordDTOToEntity(User user, UserUpdatePasswordDTO userUpdatePasswordDTO) {
        if (!passwordEncoder.matches(userUpdatePasswordDTO.getPassword() + user.getSalt(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu không đúng");
        }
        user.setPassword(passwordEncoder.encode(userUpdatePasswordDTO.getPassword() + user.getSalt()));
        redisTemplate.opsForHash().put("USER", user.getId().toString(), user);
        return user;
    }

    public UserReadDTO ReadRequestToReadDTO(UserReadRequest userReadRequest) {
        return modelMapper.map(userReadRequest, UserReadDTO.class);
    }

    public Boolean ReadDTOToEntity(User user, UserReadDTO userReadDTO) {
        return passwordEncoder.matches(userReadDTO.getPassword() + user.getSalt(), user.getPassword());
    }
    public UserLoginDTO EntityToLoginDTO(User user,String token){
        UserLoginDTO userLoginDTO = modelMapper.map(user, UserLoginDTO.class);
        userLoginDTO.setToken(token);
        if(user.getRole().equals(UserRole.ADMIN)){
            userLoginDTO.setUrl("/Dashboard");
        }
        else {
            userLoginDTO.setUrl("/");
        }
        return userLoginDTO;
    }
}