package vn.vti.clothing_shop.services.implementations;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.vti.clothing_shop.dtos.ins.UserCreateDTO;
import vn.vti.clothing_shop.dtos.ins.UserReadDTO;
import vn.vti.clothing_shop.dtos.ins.UserUpdateDTO;
import vn.vti.clothing_shop.dtos.ins.UserUpdatePasswordDTO;
import vn.vti.clothing_shop.dtos.outs.UserDTO;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.mappers.UserMapper;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.services.JwtService;
import vn.vti.clothing_shop.services.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class UserServiceImplementation implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImplementation(JwtService jwtService, UserRepository userRepository, UserMapper userMapper,PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    //@Cacheable(value = "users")
    public List<UserDTO> getAllUsers(){
        return userMapper.EntityToDTO(userRepository.findAllUser());
    };

    //@CachePut(value = "users")
    @Transactional
    public UserLoginDTO getUser(UserReadDTO userReadDTO){
        User user = userReadDTO.getUsername()!=null ?
                this.userRepository
                        .findByUsername(userReadDTO.getUsername())
                        .orElseThrow(()->new NotFoundException("Người dùng không tồn tại"))
                :(
                        userReadDTO.getEmail()!=null ?
                                this.userRepository
                                        .findByEmail(userReadDTO.getEmail())
                                        .orElseThrow(()->new NotFoundException("Người dùng không tồn tại")) :
                        this.userRepository
                                .findByPhoneNumber(userReadDTO.getPhone_number())
                                .orElseThrow(()->new NotFoundException("Người dùng không tồn tại"))
                );
        if(!userMapper.ReadDTOToEntity(user,userReadDTO)) throw new BadRequestException("Sai mật khẩu");
        return userMapper.EntityToLoginDTO(user,jwtService.generateToken(user));
    }

    //@CacheEvict(value = "users", allEntries = true)
    @Transactional
    public Boolean addUser(UserCreateDTO userCreateDTO){
        this.userRepository.findByUsername(userCreateDTO.getUsername()).ifPresent(u->{throw new BadRequestException("Tên đăng nhập đã tồn tại");});
        this.userRepository.findByEmail(userCreateDTO.getEmail()).ifPresent(u->{throw new BadRequestException("Email đã tồn tại");});
        this.userRepository.findByPhoneNumber(userCreateDTO.getPhone_number()).ifPresent(u->{throw new BadRequestException("Số điện thoại đã tồn tại");});
        userRepository.save(userMapper.createNormalUser(userCreateDTO));
        return true;
    }

    //@Cacheable(value = "users")
    public Long countUser(){
        return userRepository.count();
    };

    //@Cacheable(value = "user", key = "#id")
    public UserDTO getUserById(Long id){
        return userMapper.EntityToDTO(userRepository.findById(id).orElseThrow(()->new NotFoundException("Người dùng không tồn tại")));
    };

    //@CachePut(value = "user")
    @Transactional
    public Boolean updateUser(UserUpdateDTO userUpdateDTO){
        User user = this.userRepository.findById(userUpdateDTO.getId()).orElseThrow(()->new NotFoundException("Người dùng không tồn tại"));
        this.userRepository
                .findByEmail(userUpdateDTO.getEmail())
                .ifPresent(u->{
                    if(!Objects.equals(u.getEmail(),user.getEmail()))
                        throw new BadRequestException("Email đã tồn tại");
                });
        this.userRepository
                .findByPhoneNumber(userUpdateDTO.getPhone_number())
                .ifPresent(u->{
                    if(!Objects.equals(u.getPhone_number(),user.getPhone_number()))
                        throw new BadRequestException("Số điện thoại đã tồn tại");
                });
        this.userRepository.save(userMapper.UpdateDTOToEntity(user,userUpdateDTO));
        return true;
    }

    //@CachePut(value = "user")
    @Transactional
    public Boolean updateUserPassword(UserUpdatePasswordDTO userUpdatePasswordDTO){
        User user = this.userRepository
                .findById(userUpdatePasswordDTO.getId())
                .orElseThrow(()->new NotFoundException("Người dùng không tồn tại"));
        this.userRepository.save(userMapper.UpdatePasswordDTOToEntity(user,userUpdatePasswordDTO));
        return true;
    };

    //@CacheEvict(value = "users", allEntries = true)
    @Transactional
    public Boolean deleteUser(Long id){
        User user = this.userRepository.findById(id).orElseThrow(()->new NotFoundException("Người dùng không tồn tại"));
        user.setDeleted_at(LocalDateTime.now());
        this.userRepository.save(user);
        return true;
    };
}
