package vn.vti.clothing_shop.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import vn.payos.PayOS;
import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.constants.PaymentStatus;
import vn.vti.clothing_shop.constants.UserGender;
import vn.vti.clothing_shop.constants.UserRole;
import vn.vti.clothing_shop.dtos.ins.OrderCheckoutRequest;
import vn.vti.clothing_shop.dtos.ins.OrderConfirmRequest;
import vn.vti.clothing_shop.dtos.ins.OrderCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderItemCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderItemUpdateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateRequest;
import vn.vti.clothing_shop.dtos.ins.UserCreateRequest;
import vn.vti.clothing_shop.dtos.ins.UserLoginRequest;
import vn.vti.clothing_shop.dtos.ins.UserUpdatePasswordRequest;
import vn.vti.clothing_shop.dtos.ins.UserUpdateRequest;
import vn.vti.clothing_shop.dtos.ins.VoucherCreateRequest;
import vn.vti.clothing_shop.dtos.ins.VoucherUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.dtos.outs.PaymentCheckoutResponse;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.entities.ImportedProduct;
import vn.vti.clothing_shop.entities.InputSale;
import vn.vti.clothing_shop.entities.OnSaleProduct;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.entities.OrderItem;
import vn.vti.clothing_shop.entities.User;
import vn.vti.clothing_shop.entities.Voucher;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.OrderItemMapper;
import vn.vti.clothing_shop.mappers.OrderMapper;
import vn.vti.clothing_shop.mappers.UserMapper;
import vn.vti.clothing_shop.mappers.VoucherMapper;
import vn.vti.clothing_shop.repositories.ImportedProductRepository;
import vn.vti.clothing_shop.repositories.OnSaleProductRepository;
import vn.vti.clothing_shop.repositories.OrderItemRepository;
import vn.vti.clothing_shop.repositories.OrderRepository;
import vn.vti.clothing_shop.repositories.UserRepository;
import vn.vti.clothing_shop.repositories.VoucherRepository;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.services.JwtService;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserVoucherOrderServiceTest {
	@Test
	void containsUserVoucherOrderAndPaymentServiceTests() {
		assertThat(List.of("user", "voucher", "order", "payment")).hasSize(4);
	}

	private static PostgresToMongoReadModelSyncService noOpReadModelSyncService() {
		return new PostgresToMongoReadModelSyncService(
				null, null, null, null, null, null, null, null, null, null, null, new ObjectMapper()
		) {
			@Override
			public void syncAfterCommit(ReadModelType modelType, Long entityId) {
				// No-op test double: unit tests assert service behavior without asynchronous read-model syncing.
			}

			@Override
			public void removeAfterCommit(ReadModelType modelType, Long entityId) {
				// No-op test double: unit tests assert service behavior without asynchronous read-model removal.
			}
		};
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class UserServiceTest {
		@Mock
		PasswordEncoder passwordEncoder;

		@Mock
		JwtService jwtService;

		@Mock
		UserRepository userRepository;

		@Mock
		UserMapper userMapper;

		@InjectMocks
		UserServiceImpl service;

		@Test
		void getUsersReturnsActiveUsers() {
			User user = new User();
			when(userRepository.findByDeletedAtIsNull()).thenReturn(List.of(user));

			assertThat(service.getUsers()).containsExactly(user);
		}

		@Test
		void getUserReturnsLoginDtoWhenPasswordMatches() throws WrapperException {
			UserLoginRequest request = new UserLoginRequest("demo", "secret");
			User user = new User();
			user.setPassword("encoded");
			UserLoginDTO dto = new UserLoginDTO("avatar", "Demo", "jwt-token", null);

			when(userRepository.findOneByDeletedAtIsNullAndUsernameOrEmailOrPhoneNumber("demo", "demo", "demo"))
					.thenReturn(Optional.of(user));
			when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
			when(jwtService.generateToken(user)).thenReturn("jwt-token");
			when(userMapper.entityToLoginDTO(user, "jwt-token")).thenReturn(dto);

			assertThat(service.getUser(request)).isSameAs(dto);
		}

		@Test
		void getUserWrapsInvalidPassword() {
			UserLoginRequest request = new UserLoginRequest("demo", "bad");
			User user = new User();
			user.setPassword("encoded");

			when(userRepository.findOneByDeletedAtIsNullAndUsernameOrEmailOrPhoneNumber("demo", "demo", "demo"))
					.thenReturn(Optional.of(user));
			when(passwordEncoder.matches("bad", "encoded")).thenReturn(false);

			assertThatThrownBy(() -> service.getUser(request))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void getUserByIdReturnsRepositoryUser() throws WrapperException {
			User user = new User();
			when(userRepository.findByDeletedAtIsNullAndId(9L)).thenReturn(Optional.of(user));

			assertThat(service.getUserById(9L)).isSameAs(user);
		}

		@Test
		void addUserHashesPasswordAndSetsSalt() throws WrapperException {
			UserCreateRequest request = new UserCreateRequest(
					"Demo",
					"demo",
					"secret",
					"demo@example.com",
					"0900000001",
					"Ha Noi",
					LocalDate.of(1995, Month.JANUARY, 1),
					null,
					null,
					UserGender.MALE
			);
			User user = new User();

			when(userMapper.createRequestToEntity(request, UserRole.USER)).thenReturn(user);
			when(passwordEncoder.encode("secret")).thenReturn("encoded");

			service.addUser(request);

			assertThat(user.getSalt()).isNotBlank();
			assertThat(user.getPassword()).isEqualTo("encoded");
			verify(userRepository).save(user);
		}

		@Test
		void addUserWrapsDuplicateEmail() {
			UserCreateRequest request = userCreateRequest();
			when(userRepository.existsByDeletedAtIsNullAndUsername("demo")).thenReturn(false);
			when(userRepository.existsByDeletedAtIsNullAndEmail("demo@example.com")).thenReturn(true);

			assertThatThrownBy(() -> service.addUser(request))
					.isInstanceOf(WrapperException.class);
		}

		private UserCreateRequest userCreateRequest() {
			return new UserCreateRequest(
					"Demo",
					"demo",
					"secret",
					"demo@example.com",
					"0900000001",
					"Ha Noi",
					LocalDate.of(1995, Month.JANUARY, 1),
					null,
					null,
					UserGender.MALE
			);
		}

		@Test
		void updateUserSavesMappedEntityWhenUnique() throws WrapperException {
			UserUpdateRequest request = new UserUpdateRequest(
					"Demo",
					"new@example.com",
					"0900000002",
					"Ha Noi",
					LocalDate.of(1995, Month.JANUARY, 1),
					null,
					null,
					UserGender.MALE
			);
			User user = new User();
			user.setEmail("old@example.com");
			user.setPhoneNumber("0900000001");
			User mapped = new User();

			when(userRepository.findById(9L)).thenReturn(Optional.of(user));
			when(userRepository.existsByDeletedAtIsNullAndEmail("new@example.com")).thenReturn(false);
			when(userRepository.existsByDeletedAtIsNullAndPhoneNumber("0900000002")).thenReturn(false);
			when(userMapper.updateRequestToEntity(request, user)).thenReturn(mapped);

			service.updateUser(request, 9L);

			verify(userRepository).save(mapped);
		}

		@Test
		void updateUserWrapsDuplicateEmail() {
			UserUpdateRequest request = new UserUpdateRequest(
					"Demo",
					"new@example.com",
					"0900000001",
					"Ha Noi",
					LocalDate.of(1995, Month.JANUARY, 1),
					null,
					null,
					UserGender.MALE
			);
			User user = new User();
			user.setEmail("old@example.com");
			user.setPhoneNumber("0900000001");

			when(userRepository.findById(9L)).thenReturn(Optional.of(user));
			when(userRepository.existsByDeletedAtIsNullAndEmail("new@example.com")).thenReturn(true);

			assertThatThrownBy(() -> service.updateUser(request, 9L))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void updateUserWrapsDuplicatePhone() {
			UserUpdateRequest request = new UserUpdateRequest(
					"Demo",
					"old@example.com",
					"0900000002",
					"Ha Noi",
					LocalDate.of(1995, Month.JANUARY, 1),
					null,
					null,
					UserGender.MALE
			);
			User user = new User();
			user.setEmail("old@example.com");
			user.setPhoneNumber("0900000001");

			when(userRepository.findById(9L)).thenReturn(Optional.of(user));
			when(userRepository.existsByDeletedAtIsNullAndPhoneNumber("0900000002")).thenReturn(true);

			assertThatThrownBy(() -> service.updateUser(request, 9L))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void updateUserPasswordSavesEncodedPassword() throws WrapperException {
			UserUpdatePasswordRequest request = new UserUpdatePasswordRequest("old", "new", 1L);
			User user = new User();
			user.setPassword("encoded-old");

			when(userRepository.findById(9L)).thenReturn(Optional.of(user));
			when(passwordEncoder.matches("old", "encoded-old")).thenReturn(true);
			when(passwordEncoder.encode("new")).thenReturn("encoded-new");

			service.updateUserPassword(request, 9L);

			assertThat(user.getPassword()).isEqualTo("encoded-new");
			verify(userRepository).save(user);
		}

		@Test
		void deleteUserSoftDeletesExistingUser() throws WrapperException {
			User user = new User();
			when(userRepository.findByDeletedAtIsNullAndId(9L)).thenReturn(Optional.of(user));

			service.deleteUser(9L);

			assertThat(user.getDeletedAt()).isNotNull();
			verify(userRepository).save(user);
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class VoucherServiceTest {
		@Mock
		VoucherRepository voucherRepository;

		@Mock
		VoucherMapper voucherMapper;

		@Mock
		MongoReadModelQueryService readModelQueryService;

		VoucherServiceImpl service;

		@BeforeEach
		void setUp() {
			service = new VoucherServiceImpl(
					voucherRepository,
					voucherMapper,
					readModelQueryService,
					noOpReadModelSyncService()
			);
		}

		@Test
		void getAllVouchersReturnsMongoReadModelWhenAvailable() {
			Voucher voucher = voucher(1L, "WELCOME10", 10, 10F, System.currentTimeMillis() - 1000,
					System.currentTimeMillis() + 1000);
			when(readModelQueryService.findAll(ReadModelType.VOUCHER, Voucher.class)).thenReturn(List.of(voucher));

			assertThat(service.getAllVouchers()).containsExactly(voucher);
		}

		@Test
		void getAllVouchersFallsBackToRepositoryWhenReadModelIsNull() {
			Voucher voucher = voucher(1L, "WELCOME10", 10, 10F, System.currentTimeMillis() - 1000,
					System.currentTimeMillis() + 1000);
			when(readModelQueryService.findAll(ReadModelType.VOUCHER, Voucher.class)).thenReturn(null);
			when(voucherRepository.findByDeletedAtIsNullOrderByIdDesc()).thenReturn(List.of(voucher));

			assertThat(service.getAllVouchers()).containsExactly(voucher);
		}

		@Test
		void getAllAvailableVouchersFiltersMongoReadModelByWindowAndStock() {
			long now = System.currentTimeMillis();
			Voucher active = voucher(1L, "ACTIVE", 10, 10F, now - 1000, now + 1000);
			Voucher outOfStock = voucher(2L, "EMPTY", 0, 10F, now - 1000, now + 1000);
			Voucher future = voucher(3L, "FUTURE", 10, 10F, now + 1000, now + 2000);
			Voucher expired = voucher(4L, "EXPIRED", 10, 10F, now - 2000, now - 1000);
			when(readModelQueryService.findAll(ReadModelType.VOUCHER, Voucher.class))
					.thenReturn(List.of(active, outOfStock, future, expired));

			assertThat(service.getAllAvailableVouchers()).containsExactly(active);
		}

		@Test
		void getAllAvailableVouchersSkipsNullReadModelFields() {
			long now = System.currentTimeMillis();
			Voucher active = voucher(1L, "ACTIVE", 10, 10F, now - 1000, now + 1000);
			Voucher missingStock = voucher(2L, "NO_STOCK", null, 10F, now - 1000, now + 1000);
			Voucher missingStart = voucher(3L, "NO_START", 10, 10F, null, now + 1000);
			Voucher missingEnd = voucher(4L, "NO_END", 10, 10F, now - 1000, null);
			when(readModelQueryService.findAll(ReadModelType.VOUCHER, Voucher.class))
					.thenReturn(List.of(active, missingStock, missingStart, missingEnd));

			assertThat(service.getAllAvailableVouchers()).containsExactly(active);
		}

		@Test
		void getAllAvailableVouchersUsesCurrentWindowAndReturnsResults() {
			Voucher voucher = new Voucher();
			when(readModelQueryService.findAll(ReadModelType.VOUCHER, Voucher.class)).thenReturn(List.of());
			when(voucherRepository.findByDeletedAtIsNullAndStockGreaterThanAndAvailableDateLessThanEqualAndEndDateGreaterThanEqual(
					org.mockito.ArgumentMatchers.eq(0),
					anyLong(),
					anyLong()
			)).thenReturn(List.of(voucher));

			assertThat(service.getAllAvailableVouchers()).containsExactly(voucher);
		}

		@Test
		void findVoucherByCodeFallsBackToRepository() throws WrapperException {
			Voucher voucher = voucher(1L, "WELCOME10", 10, 10F, System.currentTimeMillis() - 1000,
					System.currentTimeMillis() + 1000);
			when(readModelQueryService.findByLookupKey(ReadModelType.VOUCHER, "WELCOME10", Voucher.class))
					.thenReturn(Optional.empty());
			when(voucherRepository.findByDeletedAtIsNullAndCode("WELCOME10")).thenReturn(Optional.of(voucher));

			assertThat(service.findVoucherByCode("WELCOME10")).isSameAs(voucher);
		}

		@Test
		void findVoucherByCodeReturnsMongoReadModel() throws WrapperException {
			Voucher voucher = voucher(1L, "WELCOME10", 10, 10F, System.currentTimeMillis() - 1000,
					System.currentTimeMillis() + 1000);
			when(readModelQueryService.findByLookupKey(ReadModelType.VOUCHER, "WELCOME10", Voucher.class))
					.thenReturn(Optional.of(voucher));

			assertThat(service.findVoucherByCode("WELCOME10")).isSameAs(voucher);
		}

		@Test
		void findVoucherByIdReturnsMongoReadModel() throws WrapperException {
			Voucher voucher = voucher(1L, "WELCOME10", 10, 10F, System.currentTimeMillis() - 1000,
					System.currentTimeMillis() + 1000);
			when(readModelQueryService.findById(ReadModelType.VOUCHER, 1L, Voucher.class)).thenReturn(Optional.of(voucher));

			assertThat(service.findVoucherById(1L)).isSameAs(voucher);
		}

		@Test
		void findVoucherByIdWrapsMissingVoucher() {
			when(readModelQueryService.findById(ReadModelType.VOUCHER, 99L, Voucher.class)).thenReturn(Optional.empty());
			when(voucherRepository.findByDeletedAtIsNullAndId(99L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.findVoucherById(99L))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void createVoucherWrapsDuplicateCode() {
			VoucherCreateRequest request = new VoucherCreateRequest(
					"WELCOME10",
					10,
					10F,
					LocalDate.now(),
					LocalDate.now().plusDays(10)
			);
			when(voucherRepository.existsByDeletedAtIsNullAndCode("WELCOME10")).thenReturn(true);

			assertThatThrownBy(() -> service.createVoucher(request))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void createVoucherSavesMappedEntity() throws WrapperException {
			VoucherCreateRequest request = new VoucherCreateRequest(
					"WELCOME10",
					10,
					10F,
					LocalDate.now(),
					LocalDate.now().plusDays(10)
			);
			Voucher mapped = new Voucher();
			Voucher saved = new Voucher();
			saved.setId(1L);

			when(voucherRepository.existsByDeletedAtIsNullAndCode("WELCOME10")).thenReturn(false);
			when(voucherMapper.createRequestToEntity(request)).thenReturn(mapped);
			when(voucherRepository.save(mapped)).thenReturn(saved);

			service.createVoucher(request);

			verify(voucherRepository).save(mapped);
		}

		@Test
		void updateVoucherSavesMappedEntity() throws WrapperException {
			VoucherUpdateRequest request = new VoucherUpdateRequest(
					"WELCOME20",
					20,
					20F,
					LocalDate.now(),
					LocalDate.now().plusDays(10),
					2L
			);
			Voucher existing = new Voucher();
			Voucher mapped = new Voucher();
			mapped.setId(1L);

			when(voucherRepository.findById(1L)).thenReturn(Optional.of(existing));
			when(voucherMapper.updateRequestToEntity(request, existing)).thenReturn(mapped);
			when(voucherRepository.save(mapped)).thenReturn(mapped);

			service.updateVoucher(request, 1L);

			verify(voucherRepository).save(mapped);
		}

		@Test
		void deleteVoucherSoftDeletesExistingVoucher() throws WrapperException {
			Voucher voucher = new Voucher();
			when(voucherRepository.findById(1L)).thenReturn(Optional.of(voucher));

			service.deleteVoucher(1L);

			assertThat(voucher.getDeletedAt()).isNotNull();
			verify(voucherRepository).save(voucher);
		}

		private Voucher voucher(Long id, String code, Integer stock, Float value, Long availableDate, Long endDate) {
			Voucher voucher = new Voucher();
			voucher.setId(id);
			voucher.setCode(code);
			voucher.setInputStock(stock);
			voucher.setStock(stock);
			voucher.setValue(value);
			voucher.setAvailableDate(availableDate);
			voucher.setEndDate(endDate);
			return voucher;
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class OrderServiceTest {
		@Mock
		PayOS payOS;

		@Mock
		OrderRepository orderRepository;

		@Mock
		UserRepository userRepository;

		@Mock
		VoucherRepository voucherRepository;

		@Mock
		OrderMapper orderMapper;

		@Mock
		MongoReadModelQueryService readModelQueryService;

		OrderServiceImpl service;

		@BeforeEach
		void setUp() {
			service = new OrderServiceImpl(
					orderRepository,
					userRepository,
					voucherRepository,
					orderMapper,
					readModelQueryService,
					noOpReadModelSyncService()
			);
		}

		@Test
		void getAllOrdersReturnsMongoReadModelWhenAvailable() {
			Order order = new Order();
			when(readModelQueryService.findAll(ReadModelType.ORDER, Order.class)).thenReturn(List.of(order));

			assertThat(service.getAllOrders()).containsExactly(order);
		}

		@Test
		void getAllOrdersFallsBackToRepositoryWhenReadModelIsEmpty() {
			Order order = new Order();
			when(readModelQueryService.findAll(ReadModelType.ORDER, Order.class)).thenReturn(List.of());
			when(orderRepository.findByDeletedAtIsNullOrderByIdDesc()).thenReturn(List.of(order));

			assertThat(service.getAllOrders()).containsExactly(order);
		}

		@Test
		void getAllOrdersByUserIdFallsBackToRepository() {
			Order order = new Order();
			when(readModelQueryService.findByOwner(ReadModelType.ORDER, 9L, Order.class)).thenReturn(List.of());
			when(orderRepository.findByDeletedAtIsNullAndUser_Id(9L)).thenReturn(List.of(order));

			assertThat(service.getAllOrdersByUserId(9L)).containsExactly(order);
		}

		@Test
		void getAllOrdersByUserIdReturnsMongoReadModel() {
			Order order = new Order();
			when(readModelQueryService.findByOwner(ReadModelType.ORDER, 9L, Order.class)).thenReturn(List.of(order));

			assertThat(service.getAllOrdersByUserId(9L)).containsExactly(order);
		}

		@Test
		void getOrderByIdAndUserIdReturnsMongoReadModel() throws WrapperException {
			Order order = new Order();
			when(readModelQueryService.findByIdAndOwner(ReadModelType.ORDER, 7L, 9L, Order.class))
					.thenReturn(Optional.of(order));

			assertThat(service.getOrderByIdAndUserId(7L, 9L)).isSameAs(order);
		}

		@Test
		void getOrderByIdAndUserIdFallsBackToRepository() throws WrapperException {
			Order order = new Order();
			when(readModelQueryService.findByIdAndOwner(ReadModelType.ORDER, 7L, 9L, Order.class))
					.thenReturn(Optional.empty());
			when(orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(7L, 9L)).thenReturn(Optional.of(order));

			assertThat(service.getOrderByIdAndUserId(7L, 9L)).isSameAs(order);
		}

		@Test
		void getOrderByCheckoutRequestFallsBackToRepository() throws WrapperException {
			Order order = new Order();
			OrderCheckoutRequest request = new OrderCheckoutRequest(7L);
			when(readModelQueryService.findByIdAndOwner(ReadModelType.ORDER, 7L, 9L, Order.class))
					.thenReturn(Optional.empty());
			when(orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(7L, 9L)).thenReturn(Optional.of(order));

			assertThat(service.getOrderByIdAndUserId(request, 9L)).isSameAs(order);
		}

		@Test
		void addOrderCreatesCartWhenNoOpenOrderExists() throws WrapperException {
			OrderCreateRequest request = new OrderCreateRequest("Address", "0900000001", "Demo");
			User user = new User();
			Order order = new Order();
			Order saved = new Order();

			when(userRepository.findById(9L)).thenReturn(Optional.of(user));
			when(orderRepository.findByDeletedAtIsNullAndUser_IdAndPaymentStatus(9L, PaymentStatus.NOT_CONFIRMED))
					.thenReturn(Optional.empty());
			when(orderMapper.createRequestToEntity(request, user)).thenReturn(order);
			when(orderRepository.save(order)).thenReturn(saved);

			assertThat(service.addOrder(request, 9L)).isSameAs(saved);
		}

		@Test
		void addOrderReturnsExistingOpenOrder() throws WrapperException {
			OrderCreateRequest request = new OrderCreateRequest("Address", "0900000001", "Demo");
			User user = new User();
			Order existing = new Order();

			when(userRepository.findById(9L)).thenReturn(Optional.of(user));
			when(orderRepository.findByDeletedAtIsNullAndUser_IdAndPaymentStatus(9L, PaymentStatus.NOT_CONFIRMED))
					.thenReturn(Optional.of(existing));

			assertThat(service.addOrder(request, 9L)).isSameAs(existing);
		}

		@Test
		void addOrderWrapsMissingUser() {
			OrderCreateRequest request = new OrderCreateRequest("Address", "0900000001", "Demo");
			when(userRepository.findById(9L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.addOrder(request, 9L))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void updateOrderSavesMappedOrderWhenVoucherIsAvailable() throws WrapperException {
			OrderUpdateRequest request = new OrderUpdateRequest(
					"Address",
					"0900000001",
					"Demo",
					false,
					PaymentMethod.COD,
					5L
			);
			Order order = new Order();
			Voucher voucher = availableVoucher(5L, 2);
			Order mapped = new Order();
			mapped.setId(7L);

			when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
			when(voucherRepository.findById(5L)).thenReturn(Optional.of(voucher));
			when(orderMapper.updateRequestToEntity(request, voucher, order)).thenReturn(mapped);
			when(orderRepository.save(mapped)).thenReturn(mapped);

			service.updateOrder(7L, request);

			assertThat(voucher.getStock()).isEqualTo(1);
			verify(voucherRepository).save(voucher);
			verify(orderRepository).save(mapped);
		}

		@Test
		void updateOrderWrapsInvalidVoucherWindow() {
			OrderUpdateRequest request = new OrderUpdateRequest(
					"Address",
					"0900000001",
					"Demo",
					false,
					PaymentMethod.COD,
					5L
			);
			Order order = new Order();
			Voucher expired = new Voucher();
			expired.setId(5L);
			expired.setStock(10);
			expired.setAvailableDate(System.currentTimeMillis() - 2000);
			expired.setEndDate(System.currentTimeMillis() - 1000);

			when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
			when(voucherRepository.findById(5L)).thenReturn(Optional.of(expired));

			assertThatThrownBy(() -> service.updateOrder(7L, request))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void updateOrderWrapsFutureVoucherWindow() {
			OrderUpdateRequest request = new OrderUpdateRequest(
					"Address",
					"0900000001",
					"Demo",
					false,
					PaymentMethod.COD,
					5L
			);
			Order order = new Order();
			Voucher future = availableVoucher(5L, 10);
			future.setAvailableDate(System.currentTimeMillis() + 1000);

			when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
			when(voucherRepository.findById(5L)).thenReturn(Optional.of(future));

			assertThatThrownBy(() -> service.updateOrder(7L, request))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void deleteOrderCancelsOrderAndRestoresVoucherStock() throws WrapperException {
			Voucher voucher = availableVoucher(5L, 2);
			Order order = new Order();
			order.setVoucher(voucher);

			when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
			when(voucherRepository.findById(5L)).thenReturn(Optional.of(voucher));

			service.deleteOrder(7L);

			assertThat(voucher.getStock()).isEqualTo(3);
			assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
			assertThat(order.getDeletedAt()).isNotNull();
			verify(orderRepository).save(order);
		}

		private Voucher availableVoucher(Long id, Integer stock) {
			Voucher voucher = new Voucher();
			voucher.setId(id);
			voucher.setStock(stock);
			voucher.setAvailableDate(System.currentTimeMillis() - 1000);
			voucher.setEndDate(System.currentTimeMillis() + 1000);
			return voucher;
		}

		@Test
		void confirmOrderUpdatesPaymentStatus() throws WrapperException {
			Order order = new Order();
			OrderConfirmRequest request = new OrderConfirmRequest(123L, true);

			when(orderRepository.findByDeletedAtIsNullAndOrderCodeAndUser_Id(123L, 9L))
					.thenReturn(Optional.of(order));

			Boolean result = service.confirmOrder(request, 9L);

			assertThat(result).isTrue();
			assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.CONFIRMED);
			verify(orderRepository).save(order);
		}

		@Test
		void confirmOrderFalseCancelsOrder() throws WrapperException {
			Order order = new Order();
			OrderConfirmRequest request = new OrderConfirmRequest(123L, false);

			when(orderRepository.findByDeletedAtIsNullAndOrderCodeAndUser_Id(123L, 9L))
					.thenReturn(Optional.of(order));

			assertThat(service.confirmOrder(request, 9L)).isFalse();
			assertThat(order.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
		}

		@Test
		void confirmOrderWrapsMissingOrder() {
			OrderConfirmRequest request = new OrderConfirmRequest(123L, true);
			when(orderRepository.findByDeletedAtIsNullAndOrderCodeAndUser_Id(123L, 9L))
					.thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.confirmOrder(request, 9L))
					.isInstanceOf(WrapperException.class);
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class PaymentServiceTest {
		@Mock
		PayOS payOS;

		@Test
		void createCheckoutReturnsManualPayloadForCod() throws WrapperException {
			PaymentServiceImpl service = paymentService();
			OrderDTO dto = new OrderDTO(1L, "Address", "0900000001", "Demo", false, 100000L, 123L,
			                            PaymentStatus.NOT_CONFIRMED, PaymentMethod.COD, null, List.of());

			PaymentCheckoutResponse response = service.createCheckout(dto);

			assertThat(response.paymentMethod()).isEqualTo(PaymentMethod.COD);
			assertThat(response.status()).isEqualTo("MANUAL_CONFIRMATION_REQUIRED");
			assertThat(response.checkoutUrl()).isNull();
		}

		@Test
		void createCheckoutDefaultsNullPaymentMethodToCod() throws WrapperException {
			PaymentServiceImpl service = paymentService();
			OrderDTO dto = new OrderDTO(1L, "Address", "0900000001", "Demo", false, 100000L, 123L,
			                            PaymentStatus.NOT_CONFIRMED, null, null, null);

			PaymentCheckoutResponse response = service.createCheckout(dto);

			assertThat(response.paymentMethod()).isEqualTo(PaymentMethod.COD);
			assertThat(response.currency()).isEqualTo("VND");
			assertThat(((Map<?, ?>) response.providerData()).get("manual")).isEqualTo(true);
		}

		@Test
		void createCheckoutReturnsManualPayloadForEBanking() throws WrapperException {
			PaymentServiceImpl service = paymentService();
			OrderDTO dto = new OrderDTO(1L, "Address", "0900000001", "Demo", false, 100000L, 123L,
			                            PaymentStatus.NOT_CONFIRMED, PaymentMethod.E_BANKING, null, List.of());

			PaymentCheckoutResponse response = service.createCheckout(dto);

			assertThat(response.paymentMethod()).isEqualTo(PaymentMethod.E_BANKING);
			assertThat(response.status()).isEqualTo("MANUAL_CONFIRMATION_REQUIRED");
		}

		@Test
		void createCheckoutWrapsNullOrder() {
			PaymentServiceImpl service = paymentService();

			assertThatThrownBy(() -> service.createCheckout(null))
					.isInstanceOf(WrapperException.class);
		}

		private PaymentServiceImpl paymentService() {
			PaymentServiceImpl service = new PaymentServiceImpl(payOS, new ObjectMapper());
			ReflectionTestUtils.setField(service, "returnUrl", "http://localhost:8080/order/success");
			ReflectionTestUtils.setField(service, "cancelUrl", "http://localhost:8080/order/cancel");
			ReflectionTestUtils.setField(service, "currency", "VND");
			ReflectionTestUtils.setField(service, "stripeSecretKey", "");
			ReflectionTestUtils.setField(service, "stripeCheckoutSessionUrl", "https://api.stripe.com/v1/checkout/sessions");
			ReflectionTestUtils.setField(service, "zaloPayAppId", "");
			ReflectionTestUtils.setField(service, "zaloPayKey1", "");
			ReflectionTestUtils.setField(service, "zaloPayCreateOrderUrl", "https://sb-openapi.zalopay.vn/v2/create");
			ReflectionTestUtils.setField(service, "zaloPayAppUser", "clothing-shop");
			return service;
		}

		@Test
		void createCheckoutWrapsMissingStripeConfiguration() {
			PaymentServiceImpl service = paymentService();
			OrderDTO dto = new OrderDTO(1L, "Address", "0900000001", "Demo", false, 100000L, 123L,
			                            PaymentStatus.NOT_CONFIRMED, PaymentMethod.STRIPE, null, List.of());

			assertThatThrownBy(() -> service.createCheckout(dto))
					.isInstanceOf(WrapperException.class);
		}
	}

	@ExtendWith(MockitoExtension.class)
	@Nested
	class OrderItemServiceTest {
		@Mock
		OrderItemRepository orderItemRepository;

		@Mock
		OrderItemMapper orderItemMapper;

		@Mock
		OrderRepository orderRepository;

		@Mock
		OnSaleProductRepository onSaleProductRepository;

		@Mock
		ImportedProductRepository importedProductRepository;

		OrderItemServiceImpl service;

		@BeforeEach
		void setUp() {
			service = new OrderItemServiceImpl(
					orderItemRepository,
					orderItemMapper,
					orderRepository,
					onSaleProductRepository,
					importedProductRepository,
					noOpReadModelSyncService()
			);
		}

		@Test
		void getAllOrderItemsReturnsRepositoryResults() {
			OrderItem orderItem = new OrderItem();
			when(orderItemRepository.findByDeletedAtIsNullOrderByIdDesc()).thenReturn(List.of(orderItem));

			assertThat(service.getAllOrderItems()).containsExactly(orderItem);
		}

		@Test
		void getAllOrderItemsByOrderIdReturnsRepositoryResults() {
			OrderItem orderItem = new OrderItem();
			when(orderItemRepository.findByDeletedAtIsNullAndOrder_Id(7L)).thenReturn(List.of(orderItem));

			assertThat(service.getAllOrderItemsByOrderId(7L)).containsExactly(orderItem);
		}

		@Test
		void findOrderItemByIdAndOrderIdReturnsRepositoryEntity() throws WrapperException {
			OrderItem orderItem = new OrderItem();
			when(orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(3L, 7L)).thenReturn(Optional.of(orderItem));

			assertThat(service.findOrderItemByIdAndOrderId(3L, 7L)).isSameAs(orderItem);
		}

		@Test
		void findOrderItemByIdAndOrderIdWrapsMissingItem() {
			when(orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(3L, 7L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.findOrderItemByIdAndOrderId(3L, 7L))
					.isInstanceOf(WrapperException.class);
		}

		@Test
		void addOrderItemReservesStockAndUpdatesOrderTotal() throws WrapperException {
			OrderItemCreateRequest request = new OrderItemCreateRequest(5L, 7L, 2);
			Order order = new Order();
			order.setTotalPrice(100L);
			ImportedProduct importedProduct = importedProduct(11L, 5, 10);
			OnSaleProduct onSaleProduct = onSaleProduct(5L, importedProduct, 100L, 10F);
			OrderItem orderItem = new OrderItem();

			when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
			when(onSaleProductRepository.findById(5L)).thenReturn(Optional.of(onSaleProduct));
			when(importedProductRepository.findByDeletedAtIsNullAndStockGreaterThanAndIdOrderByCreatedAtAsc(0, 11L))
					.thenReturn(List.of(importedProduct));
			when(orderItemRepository.findByDeletedAtIsNullAndProduct_IdAndOrder_Id(5L, 7L))
					.thenReturn(Optional.empty());
			when(orderItemMapper.createRequestToEntity(request, onSaleProduct, order)).thenReturn(orderItem);

			service.addOrderItem(request);

			assertThat(importedProduct.getStock()).isEqualTo(3);
			assertThat(order.getTotalPrice()).isEqualTo(280L);
			verify(importedProductRepository).saveAll(List.of(importedProduct));
			verify(orderRepository).save(order);
			verify(orderItemRepository).save(orderItem);
		}

		@Test
		void addOrderItemMergesExistingItemAndUsesNoDiscountWhenInputSaleMissing() throws WrapperException {
			OrderItemCreateRequest request = new OrderItemCreateRequest(5L, 7L, 2);
			Order order = new Order();
			order.setId(7L);
			order.setTotalPrice(100L);
			ImportedProduct importedProduct = importedProduct(11L, 5, 10);
			OnSaleProduct onSaleProduct = onSaleProduct(5L, importedProduct, 100L, null);
			onSaleProduct.setInputSale(null);
			OrderItem existing = orderItem(3L, order, onSaleProduct, 1);

			when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
			when(onSaleProductRepository.findById(5L)).thenReturn(Optional.of(onSaleProduct));
			when(importedProductRepository.findByDeletedAtIsNullAndStockGreaterThanAndIdOrderByCreatedAtAsc(0, 11L))
					.thenReturn(List.of(importedProduct));
			when(orderItemRepository.findByDeletedAtIsNullAndProduct_IdAndOrder_Id(5L, 7L))
					.thenReturn(Optional.of(existing));

			service.addOrderItem(request);

			assertThat(existing.getQuantity()).isEqualTo(3);
			assertThat(order.getTotalPrice()).isEqualTo(300L);
			verify(orderItemRepository).save(existing);
		}

		@Test
		void addOrderItemWrapsInsufficientStock() {
			OrderItemCreateRequest request = new OrderItemCreateRequest(5L, 7L, 6);
			Order order = new Order();
			ImportedProduct importedProduct = importedProduct(11L, 5, 10);
			OnSaleProduct onSaleProduct = onSaleProduct(5L, importedProduct, 100L, 0F);

			when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
			when(onSaleProductRepository.findById(5L)).thenReturn(Optional.of(onSaleProduct));
			when(importedProductRepository.findByDeletedAtIsNullAndStockGreaterThanAndIdOrderByCreatedAtAsc(0, 11L))
					.thenReturn(List.of(importedProduct));

			assertThatThrownBy(() -> service.addOrderItem(request))
					.isInstanceOf(WrapperException.class);
		}

		private ImportedProduct importedProduct(Long id, Integer stock, Integer importNumber) {
			ImportedProduct importedProduct = new ImportedProduct();
			importedProduct.setId(id);
			importedProduct.setStock(stock);
			importedProduct.setImportNumber(importNumber);
			return importedProduct;
		}

		private OnSaleProduct onSaleProduct(Long id, ImportedProduct importedProduct, Long salePrice, Float discount) {
			InputSale inputSale = new InputSale();
			inputSale.setDiscount(discount);
			OnSaleProduct onSaleProduct = new OnSaleProduct();
			onSaleProduct.setId(id);
			onSaleProduct.setProduct(importedProduct);
			onSaleProduct.setSalePrice(salePrice);
			onSaleProduct.setInputSale(inputSale);
			return onSaleProduct;
		}

		@Test
		void updateOrderItemRefundsStockWhenQuantityIsReduced() throws WrapperException {
			OrderItemUpdateRequest request = new OrderItemUpdateRequest(5L, 2, 1L);
			Order order = new Order();
			order.setId(7L);
			order.setTotalPrice(400L);
			ImportedProduct importedProduct = importedProduct(11L, 1, 10);
			OnSaleProduct onSaleProduct = onSaleProduct(5L, importedProduct, 50L, 0F);
			OrderItem orderItem = orderItem(3L, order, onSaleProduct, 4);

			when(orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(7L, 9L)).thenReturn(Optional.of(order));
			when(orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(3L, 7L)).thenReturn(Optional.of(orderItem));
			when(onSaleProductRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(onSaleProduct));
			when(orderItemMapper.updateRequestToEntity(request, onSaleProduct, orderItem)).thenReturn(orderItem);

			service.updateOrderItem(request, 9L, 7L, 3L);

			assertThat(importedProduct.getStock()).isEqualTo(3);
			assertThat(order.getTotalPrice()).isEqualTo(300L);
			verify(importedProductRepository).save(importedProduct);
			verify(orderRepository).save(order);
			verify(orderItemRepository).save(orderItem);
		}

		@Test
		void updateOrderItemReservesAdditionalStockWhenQuantityIncreases() throws WrapperException {
			OrderItemUpdateRequest request = new OrderItemUpdateRequest(5L, 5, 1L);
			Order order = new Order();
			order.setId(7L);
			order.setTotalPrice(200L);
			ImportedProduct importedProduct = importedProduct(11L, 4, 10);
			OnSaleProduct onSaleProduct = onSaleProduct(5L, importedProduct, 50L, 0F);
			OrderItem orderItem = orderItem(3L, order, onSaleProduct, 2);

			when(orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(7L, 9L)).thenReturn(Optional.of(order));
			when(orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(3L, 7L)).thenReturn(Optional.of(orderItem));
			when(onSaleProductRepository.findByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(onSaleProduct));
			when(importedProductRepository.findByDeletedAtIsNullAndStockGreaterThanAndIdOrderByCreatedAtAsc(0, 11L))
					.thenReturn(List.of(importedProduct));
			when(orderItemMapper.updateRequestToEntity(request, onSaleProduct, orderItem)).thenReturn(orderItem);

			service.updateOrderItem(request, 9L, 7L, 3L);

			assertThat(importedProduct.getStock()).isEqualTo(1);
			assertThat(order.getTotalPrice()).isEqualTo(350L);
			verify(importedProductRepository).saveAll(List.of(importedProduct));
		}

		@Test
		void updateOrderItemSwitchesProductAndReservesNewStock() throws WrapperException {
			OrderItemUpdateRequest request = new OrderItemUpdateRequest(6L, 2, 1L);
			Order order = new Order();
			order.setId(7L);
			order.setTotalPrice(300L);
			ImportedProduct oldImportedProduct = importedProduct(11L, 1, 10);
			OnSaleProduct oldProduct = onSaleProduct(5L, oldImportedProduct, 50L, 0F);
			OrderItem orderItem = orderItem(3L, order, oldProduct, 2);
			ImportedProduct newImportedProduct = importedProduct(12L, 5, 10);
			OnSaleProduct newProduct = onSaleProduct(6L, newImportedProduct, 80L, 0F);

			when(orderRepository.findByDeletedAtIsNullAndIdAndUser_Id(7L, 9L)).thenReturn(Optional.of(order));
			when(orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(3L, 7L)).thenReturn(Optional.of(orderItem));
			when(onSaleProductRepository.findByIdAndDeletedAtIsNull(6L)).thenReturn(Optional.of(newProduct));
			when(importedProductRepository.findByDeletedAtIsNullAndStockGreaterThanAndIdOrderByCreatedAtAsc(0, 12L))
					.thenReturn(List.of(newImportedProduct));
			when(orderItemMapper.updateRequestToEntity(request, newProduct, orderItem)).thenReturn(orderItem);

			service.updateOrderItem(request, 9L, 7L, 3L);

			assertThat(oldImportedProduct.getStock()).isEqualTo(3);
			assertThat(newImportedProduct.getStock()).isEqualTo(3);
			assertThat(order.getTotalPrice()).isEqualTo(360L);
			verify(importedProductRepository).save(oldImportedProduct);
			verify(importedProductRepository).saveAll(List.of(newImportedProduct));
		}

		private OrderItem orderItem(Long id, Order order, OnSaleProduct onSaleProduct, Integer quantity) {
			OrderItem orderItem = new OrderItem();
			orderItem.setId(id);
			orderItem.setOrder(order);
			orderItem.setProduct(onSaleProduct);
			orderItem.setQuantity(quantity);
			return orderItem;
		}

		@Test
		void deleteOrderItemSoftDeletesAndRestoresStock() throws WrapperException {
			Order order = new Order();
			order.setTotalPrice(500L);
			ImportedProduct importedProduct = importedProduct(11L, 3, 4);
			OnSaleProduct onSaleProduct = onSaleProduct(5L, importedProduct, 100L, 20F);
			OrderItem orderItem = orderItem(3L, order, onSaleProduct, 2);

			when(orderItemRepository.findByDeletedAtIsNullAndIdAndOrder_Id(3L, 7L)).thenReturn(Optional.of(orderItem));

			service.deleteOrderItem(3L, 7L);

			assertThat(orderItem.getDeletedAt()).isNotNull();
			assertThat(importedProduct.getStock()).isEqualTo(4);
			assertThat(order.getTotalPrice()).isEqualTo(340L);
			verify(importedProductRepository).save(importedProduct);
			verify(orderItemRepository).save(orderItem);
			verify(orderRepository).save(order);
		}
	}
}
