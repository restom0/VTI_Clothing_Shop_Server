package vn.vti.clothing_shop.modules;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.dtos.ins.OrderCreateRequest;
import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.dtos.outs.PaymentCheckoutResponse;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.entities.Order;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.grpc.GrpcAuthLoginRequest;
import vn.vti.clothing_shop.grpc.GrpcAuthLoginResponse;
import vn.vti.clothing_shop.grpc.GrpcPaymentCheckoutRequest;
import vn.vti.clothing_shop.grpc.GrpcPaymentCheckoutResponse;
import vn.vti.clothing_shop.grpc.GrpcReadRequest;
import vn.vti.clothing_shop.grpc.GrpcReadResponse;
import vn.vti.clothing_shop.grpc.GrpcValidateTokenRequest;
import vn.vti.clothing_shop.grpc.GrpcValidateTokenResponse;
import vn.vti.clothing_shop.grpc.GrpcWriteCommandRequest;
import vn.vti.clothing_shop.grpc.GrpcWriteCommandResponse;
import vn.vti.clothing_shop.mappers.OrderMapper;
import vn.vti.clothing_shop.modules.auth.AuthModuleGrpcService;
import vn.vti.clothing_shop.modules.payment.PaymentModuleGrpcService;
import vn.vti.clothing_shop.modules.read.ReadModuleGrpcService;
import vn.vti.clothing_shop.modules.write.WriteModuleGrpcService;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.services.JwtService;
import vn.vti.clothing_shop.services.impl.MongoReadModelQueryService;
import vn.vti.clothing_shop.services.impl.PostgresToMongoReadModelSyncService;
import vn.vti.clothing_shop.services.interfaces.OrderService;
import vn.vti.clothing_shop.services.interfaces.PaymentService;
import vn.vti.clothing_shop.services.interfaces.UserService;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GrpcModuleServicesTest {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void authLoginReturnsSerializedUserAndErrors() throws Exception {
		UserService userService = mock(UserService.class);
		JwtService jwtService = mock(JwtService.class);
		AuthModuleGrpcService service = new AuthModuleGrpcService(userService, jwtService, objectMapper);
		when(userService.getUser(any())).thenReturn(new UserLoginDTO("avatar.png", "Ada", "jwt-token", "/home"));

		CollectingObserver<GrpcAuthLoginResponse> successObserver = new CollectingObserver<>();
		service.login(GrpcAuthLoginRequest.newBuilder()
		                                  .setUsernameOrEmailOrPhoneNumber("ada@example.com")
		                                  .setPassword("secret")
		                                  .build(), successObserver);

		assertThat(successObserver.completed).isTrue();
		assertThat(successObserver.value.getSuccess()).isTrue();
		assertThat(successObserver.value.getToken()).isEqualTo("jwt-token");
		assertThat(successObserver.value.getUserJson()).contains("\"name\":\"Ada\"");

		when(userService.getUser(any())).thenThrow(new WrapperException(new BadRequestException("bad-login")));
		CollectingObserver<GrpcAuthLoginResponse> errorObserver = new CollectingObserver<>();
		service.login(GrpcAuthLoginRequest.newBuilder().build(), errorObserver);
		assertThat(errorObserver.value.getSuccess()).isFalse();
		assertThat(errorObserver.value.getMessage()).isEqualTo("bad-login");
	}

	@Test
	void authValidateTokenHandlesValidExpiredAndInvalidTokens() {
		UserService userService = mock(UserService.class);
		JwtService jwtService = mock(JwtService.class);
		AuthModuleGrpcService service = new AuthModuleGrpcService(userService, jwtService, objectMapper);
		Date expiresAt = Date.from(Instant.parse("2026-07-16T00:00:00Z"));
		when(jwtService.extractId("valid")).thenReturn("42");
		when(jwtService.<Date>extractClaim(eq("valid"), any())).thenReturn(expiresAt);
		when(jwtService.isTokenExpired("valid")).thenReturn(false);
		when(jwtService.extractId("expired")).thenReturn("42");
		when(jwtService.<Date>extractClaim(eq("expired"), any())).thenReturn(expiresAt);
		when(jwtService.isTokenExpired("expired")).thenReturn(true);
		when(jwtService.extractId("bad")).thenThrow(new IllegalArgumentException("broken"));

		CollectingObserver<GrpcValidateTokenResponse> validObserver = new CollectingObserver<>();
		service.validateToken(GrpcValidateTokenRequest.newBuilder().setToken("valid").build(), validObserver);
		assertThat(validObserver.value.getValid()).isTrue();
		assertThat(validObserver.value.getMessage()).isEqualTo("messages.auth.tokenValid");
		assertThat(validObserver.value.getSubject()).isEqualTo("42");
		assertThat(validObserver.value.getExpiresAtEpochMs()).isEqualTo(expiresAt.toInstant().toEpochMilli());

		CollectingObserver<GrpcValidateTokenResponse> expiredObserver = new CollectingObserver<>();
		service.validateToken(GrpcValidateTokenRequest.newBuilder().setToken("expired").build(), expiredObserver);
		assertThat(expiredObserver.value.getValid()).isFalse();
		assertThat(expiredObserver.value.getMessage()).isEqualTo("messages.auth.tokenExpired");

		CollectingObserver<GrpcValidateTokenResponse> badObserver = new CollectingObserver<>();
		service.validateToken(GrpcValidateTokenRequest.newBuilder().setToken("bad").build(), badObserver);
		assertThat(badObserver.value.getValid()).isFalse();
		assertThat(badObserver.value.getMessage()).isEqualTo("broken");
	}

	@Test
	void paymentCheckoutSerializesSuccessAndHandlesErrors() throws Exception {
		PaymentService paymentService = mock(PaymentService.class);
		PaymentModuleGrpcService service = new PaymentModuleGrpcService(paymentService, objectMapper);
		OrderDTO orderDTO = orderDto();
		PaymentCheckoutResponse checkout = new PaymentCheckoutResponse(PaymentMethod.COD, 555L, 100L, "VND",
		                                                               "MANUAL", null, "return", "cancel",
		                                                               Map.of("manual", true));
		when(paymentService.createCheckout(any(OrderDTO.class))).thenReturn(checkout);

		CollectingObserver<GrpcPaymentCheckoutResponse> successObserver = new CollectingObserver<>();
		service.createCheckout(GrpcPaymentCheckoutRequest.newBuilder()
		                                                .setOrderJson(objectMapper.writeValueAsString(orderDTO))
		                                                .build(), successObserver);
		assertThat(successObserver.value.getSuccess()).isTrue();
		assertThat(successObserver.value.getCheckoutJson()).contains("\"paymentMethod\":\"COD\"");

		CollectingObserver<GrpcPaymentCheckoutResponse> errorObserver = new CollectingObserver<>();
		service.createCheckout(GrpcPaymentCheckoutRequest.newBuilder().setOrderJson("{").build(), errorObserver);
		assertThat(errorObserver.value.getSuccess()).isFalse();
		assertThat(errorObserver.value.getMessage()).isNotBlank();
	}

	@Test
	void readModuleQueriesByLookupIdOwnerAndLists() {
		MongoReadModelQueryService queryService = mock(MongoReadModelQueryService.class);
		ReadModuleGrpcService service = new ReadModuleGrpcService(queryService, objectMapper);
		BrandDTO brand = new BrandDTO(1L, "Nike", "Shoes");
		when(queryService.findByLookupKey(ReadModelType.BRAND, "nike", BrandDTO.class)).thenReturn(
				java.util.Optional.of(brand));
		when(queryService.findById(ReadModelType.BRAND, 1L, BrandDTO.class)).thenReturn(java.util.Optional.of(brand));
		when(queryService.findByIdAndOwner(ReadModelType.ORDER, 7L, 9L, OrderDTO.class)).thenReturn(
				java.util.Optional.of(orderDto()));
		when(queryService.findByOwner(ReadModelType.ORDER, 9L, OrderDTO.class)).thenReturn(List.of(orderDto()));
		when(queryService.findAll(ReadModelType.BRAND, BrandDTO.class)).thenReturn(List.of(brand));

		CollectingObserver<GrpcReadResponse> lookupObserver = new CollectingObserver<>();
		service.query(GrpcReadRequest.newBuilder().setModel("brand").setLookupKey("nike").build(), lookupObserver);
		assertThat(lookupObserver.value.getSuccess()).isTrue();
		assertThat(lookupObserver.value.getItemJson()).contains("\"name\":\"Nike\"");

		CollectingObserver<GrpcReadResponse> idObserver = new CollectingObserver<>();
		service.query(GrpcReadRequest.newBuilder().setModel("BRAND").setEntityId(1L).build(), idObserver);
		assertThat(idObserver.value.getItemJson()).contains("\"id\":1");

		CollectingObserver<GrpcReadResponse> ownerItemObserver = new CollectingObserver<>();
		service.query(GrpcReadRequest.newBuilder().setModel("ORDER").setEntityId(7L).setOwnerId(9L).build(),
		              ownerItemObserver);
		assertThat(ownerItemObserver.value.getItemJson()).contains("\"orderCode\":555");

		CollectingObserver<GrpcReadResponse> ownerListObserver = new CollectingObserver<>();
		service.query(GrpcReadRequest.newBuilder().setModel("ORDER").setOwnerId(9L).build(), ownerListObserver);
		assertThat(ownerListObserver.value.getItemsJsonList()).hasSize(1);

		CollectingObserver<GrpcReadResponse> listObserver = new CollectingObserver<>();
		service.query(GrpcReadRequest.newBuilder().setModel("BRAND").build(), listObserver);
		assertThat(listObserver.value.getItemsJsonList()).hasSize(1);

		CollectingObserver<GrpcReadResponse> errorObserver = new CollectingObserver<>();
		service.query(GrpcReadRequest.newBuilder().setModel("missing").build(), errorObserver);
		assertThat(errorObserver.value.getSuccess()).isFalse();
		assertThat(errorObserver.value.getMessage()).contains("No enum constant");
	}

	@Test
	void writeModuleExecutesOrderAndReadModelCommands() throws Exception {
		OrderService orderService = mock(OrderService.class);
		PostgresToMongoReadModelSyncService readModelSyncService = mock(PostgresToMongoReadModelSyncService.class);
		OrderMapper orderMapper = mock(OrderMapper.class);
		WriteModuleGrpcService service = new WriteModuleGrpcService(orderService, readModelSyncService, objectMapper,
		                                                            orderMapper);
		Order order = new Order();
		order.setId(7L);
		when(orderService.addOrder(isNull(OrderCreateRequest.class), eq(9L))).thenReturn(order);
		when(orderMapper.entityToDTO(order)).thenReturn(orderDto());
		when(orderService.confirmOrder(any(), eq(9L))).thenReturn(true);

		CollectingObserver<GrpcWriteCommandResponse> addObserver = execute(service, "order", "add_cart", "", 9L);
		assertThat(addObserver.value.getSuccess()).isTrue();
		assertThat(addObserver.value.getResultJson()).contains("\"orderCode\":555");

		CollectingObserver<GrpcWriteCommandResponse> confirmObserver = execute(service, "ORDER", "CONFIRM",
		                                                                       "{\"orderCode\":555,\"status\":true}",
		                                                                       9L);
		assertThat(confirmObserver.value.getResultJson()).isEqualTo("true");

		CollectingObserver<GrpcWriteCommandResponse> updateObserver = execute(service, "ORDER", "UPDATE",
		                                                                      """
				                                                                      {"id":7,"request":{"address":"Street","phoneNumber":"0912345678","receiverName":"Ada","isPresent":false,"paymentMethod":"COD"}}
				                                                                      """,
		                                                                      9L);
		assertThat(updateObserver.value.getSuccess()).isTrue();
		verify(orderService).updateOrder(eq(7L), any());

		CollectingObserver<GrpcWriteCommandResponse> deleteObserver = execute(service, "ORDER", "DELETE", "{\"id\":7}",
		                                                                      9L);
		assertThat(deleteObserver.value.getMessage()).isEqualTo("messages.orders.deleted");
		verify(orderService).deleteOrder(7L);

		CollectingObserver<GrpcWriteCommandResponse> syncObserver = execute(service, "READ_MODEL", "SYNC",
		                                                                    "{\"model\":\"BRAND\",\"entityId\":1}",
		                                                                    9L);
		assertThat(syncObserver.value.getSuccess()).isTrue();
		verify(readModelSyncService).sync(ReadModelType.BRAND, 1L);

		CollectingObserver<GrpcWriteCommandResponse> removeObserver = execute(service, "READ_MODEL", "REMOVE",
		                                                                      "{\"model\":\"BRAND\",\"entityId\":1}",
		                                                                      9L);
		assertThat(removeObserver.value.getMessage()).isEqualTo("messages.read.removed");
		verify(readModelSyncService).remove(ReadModelType.BRAND, 1L);

		CollectingObserver<GrpcWriteCommandResponse> unsupportedObserver = execute(service, "CATALOG", "NOOP", "{}", 9L);
		assertThat(unsupportedObserver.value.getSuccess()).isFalse();
		assertThat(unsupportedObserver.value.getMessage()).isEqualTo("Unsupported command CATALOG.NOOP");

		CollectingObserver<GrpcWriteCommandResponse> badNumberObserver = execute(service, "ORDER", "DELETE",
		                                                                         "{\"id\":\"bad\"}", 9L);
		assertThat(badNumberObserver.value.getSuccess()).isFalse();
		assertThat(badNumberObserver.value.getMessage()).isEqualTo("id must be a number");
	}

	private static CollectingObserver<GrpcWriteCommandResponse> execute(
			WriteModuleGrpcService service,
			String aggregate,
			String action,
			String payload,
			long actorId
	) {
		CollectingObserver<GrpcWriteCommandResponse> observer = new CollectingObserver<>();
		service.executeCommand(GrpcWriteCommandRequest.newBuilder()
		                                             .setAggregate(aggregate)
		                                             .setAction(action)
		                                             .setPayloadJson(payload)
		                                             .setActorId(actorId)
		                                             .build(), observer);
		return observer;
	}

	private static OrderDTO orderDto() {
		return new OrderDTO(7L, "Street", "0912345678", "Ada", false, 100L, 555L, null, PaymentMethod.COD, null,
		                    List.of());
	}

	private static final class CollectingObserver<T> implements StreamObserver<T> {
		private T value;
		private Throwable error;
		private boolean completed;

		@Override
		public void onNext(T value) {
			this.value = value;
		}

		@Override
		public void onError(Throwable throwable) {
			this.error = throwable;
		}

		@Override
		public void onCompleted() {
			completed = true;
		}
	}
}
