package vn.vti.clothing_shop.services.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import vn.vti.clothing_shop.constants.Messages;
import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.dtos.ins.UserCreateRequest;
import vn.vti.clothing_shop.dtos.ins.UserLoginRequest;
import vn.vti.clothing_shop.dtos.ins.UserUpdatePasswordRequest;
import vn.vti.clothing_shop.dtos.ins.UserUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.exceptions.NotFoundException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.UserMapper;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.services.JwtService;
import vn.vti.clothing_shop.services.interfaces.UserService;
import vn.vti.clothing_shop.utils.TimeUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UserServiceImpl implements UserService {
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	//@Cacheable(value = "users")
	@Override
	public List<User> getUsers() {
		return userRepository.findByDeletedAtIsNull();
	}

	//@CachePut(value = "users")
	@Transactional
	@Override
	public UserLoginDTO getUser(UserLoginRequest userLoginRequest) throws WrapperException {
		try {
			final String keyword = userLoginRequest.usernameOrEmailOrPhoneNumber();
			final User user = userRepository.findOneByDeletedAtIsNullAndUsernameOrEmailOrPhoneNumber(keyword, keyword, keyword)
			                                .orElseThrow(() -> new NotFoundException("messages.users.accountNotFound"));

			if (!passwordEncoder.matches(userLoginRequest.password(), user.getPassword())) {
				throw new BadRequestException("messages.users.invalidCredentials");
			}
			return userMapper.entityToLoginDTO(user, jwtService.generateToken(user));
		} catch (NotFoundException | BadRequestException ex) {
			throw new WrapperException(ex);
		}
	}

	//@CacheEvict(value = "users", allEntries = true)
	@Transactional
	@Override
	public void addUser(UserCreateRequest userCreateRequest) throws WrapperException {
		try {
			if (userRepository.existsByDeletedAtIsNullAndUsername(userCreateRequest.username())) {
				throw new BadRequestException("messages.users.usernameExists");
			}
			if (userRepository.existsByDeletedAtIsNullAndEmail(userCreateRequest.email())) {
				throw new BadRequestException("messages.users.emailExists");
			}
			if (userRepository.existsByDeletedAtIsNullAndPhoneNumber(userCreateRequest.phoneNumber())) {
				throw new BadRequestException("messages.users.phoneExists");
			}
			User user = userMapper.createRequestToEntity(userCreateRequest, UserRole.USER);
			user.setSalt(UUID.randomUUID().toString());
			user.setPassword(passwordEncoder.encode(userCreateRequest.password()));
			userRepository.save(user);
		} catch (BadRequestException e) {
			throw new WrapperException(e);
		}
	}

	//@Cacheable(value = "users")
	@Override
	public Long countUser() {
		return userRepository.count();
	}

	//@Cacheable(value = "user", key = "#id")
	@Override
	public User getUserById(Long id) throws WrapperException {
		try {
			return userRepository.findByDeletedAtIsNullAndId(id).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_USER_NOTFOUND));
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}

	//@CachePut(value = "user")
	@Transactional
	@Override
	public void updateUser(UserUpdateRequest userUpdateRequest, Long userId) throws WrapperException {
		try {
			User user = userRepository.findById(userId).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_USER_NOTFOUND));
			if (!Objects.equals(user.getEmail(), userUpdateRequest.email())
					&& userRepository.existsByDeletedAtIsNullAndEmail(userUpdateRequest.email())) {
				throw new BadRequestException("messages.users.emailExists");
			}
			if (userRepository.existsByDeletedAtIsNullAndPhoneNumber(userUpdateRequest.phoneNumber()) && !Objects.equals(
					user.getPhoneNumber(), userUpdateRequest.phoneNumber())) {
				throw new BadRequestException("messages.users.phoneExists");
			}
			userRepository.save(userMapper.updateRequestToEntity(userUpdateRequest, user));
		} catch (NotFoundException | BadRequestException e) {
			throw new WrapperException(e);
		}
	}

	//@CachePut(value = "user")
	@Transactional
	@Override
	public void updateUserPassword(UserUpdatePasswordRequest userUpdatePasswordRequest, Long userId) throws WrapperException {
		try {
			User user = userRepository
					.findById(userId)
					.orElseThrow(() -> new NotFoundException(Messages.MESSAGE_USER_NOTFOUND));

			if (!passwordEncoder.matches(userUpdatePasswordRequest.oldPassword(), user.getPassword())) {
				throw new BadRequestException("messages.users.passwordInvalid");
			}

			user.setPassword(passwordEncoder.encode(userUpdatePasswordRequest.password()));
			userRepository.save(user);
		} catch (NotFoundException | BadRequestException e) {
			throw new WrapperException(e);
		}
	}

	//@CacheEvict(value = "users", allEntries = true)
	@Transactional
	@Override
	public void deleteUser(Long id) throws WrapperException {
		try {
			User user = userRepository.findByDeletedAtIsNullAndId(id).orElseThrow(
					() -> new NotFoundException(Messages.MESSAGE_USER_NOTFOUND));
			user.setDeletedAt(TimeUtils.currentEpochMillis());
			userRepository.save(user);
		} catch (NotFoundException e) {
			throw new WrapperException(e);
		}
	}
}
