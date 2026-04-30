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
import vn.vti.clothing_shop.dtos.ins.OrderConfirmRequest;
import vn.vti.clothing_shop.dtos.ins.OrderCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderItemCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderItemUpdateRequest;
import vn.vti.clothing_shop.dtos.ins.UserCreateRequest;
import vn.vti.clothing_shop.dtos.ins.UserLoginRequest;
import vn.vti.clothing_shop.dtos.ins.VoucherCreateRequest;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserVoucherOrderServiceTest {

	private static PostgresToMongoReadModelSyncService noOpReadModelSyncService() {
		return new PostgresToMongoReadModelSyncService(
				null, null, null, null, null, null, null, null, null, null, null, new ObjectMapper()
		) {
			@Override
			public void syncAfterCommit(ReadModelType modelType, Long entityId) {
			}

			@Override
			public void removeAfterCommit(ReadModelType modelType, Long entityId) {
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
		void addUserHashesPasswordAndSetsSalt() throws WrapperException {
			UserCreateRequest request = new UserCreateRequest(
					"Demo",
					"demo",
					"secret",
					"demo@example.com",
					"0900000001",
					"Ha Noi",
					LocalDate.of(1995, 1, 1),
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
		void getAllAvailableVouchersUsesCurrentWindowAndReturnsResults() {
			Voucher voucher = new Voucher();
			when(voucherRepository.findByDeletedAtIsNullAndStockGreaterThanAndAvailableDateLessThanEqualAndEndDateGreaterThanEqual(
					org.mockito.ArgumentMatchers.eq(0),
					anyLong(),
					anyLong()
			)).thenReturn(List.of(voucher));

			assertThat(service.getAllAvailableVouchers()).containsExactly(voucher);
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
