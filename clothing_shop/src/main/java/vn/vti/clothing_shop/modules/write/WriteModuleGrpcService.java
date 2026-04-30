package vn.vti.clothing_shop.modules.write;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.dtos.ins.OrderConfirmRequest;
import vn.vti.clothing_shop.dtos.ins.OrderCreateRequest;
import vn.vti.clothing_shop.dtos.ins.OrderUpdateRequest;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.grpc.GrpcWriteCommandRequest;
import vn.vti.clothing_shop.grpc.GrpcWriteCommandResponse;
import vn.vti.clothing_shop.grpc.WriteModuleServiceGrpc;
import vn.vti.clothing_shop.mappers.OrderMapper;
import vn.vti.clothing_shop.modules.grpc.GrpcResponseSupport;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.services.impl.PostgresToMongoReadModelSyncService;
import vn.vti.clothing_shop.services.interfaces.OrderService;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "microservices.modules.write", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WriteModuleGrpcService extends WriteModuleServiceGrpc.WriteModuleServiceImplBase {
	private final OrderService orderService;
	private final PostgresToMongoReadModelSyncService readModelSyncService;
	private final ObjectMapper objectMapper;
	private final OrderMapper orderMapper;

	@Override
	public void executeCommand(GrpcWriteCommandRequest request, StreamObserver<GrpcWriteCommandResponse> responseObserver) {
		try {
			GrpcWriteCommandResponse response = switch (normalize(request.getAggregate())) {
				case "ORDER" -> handleOrderCommand(request);
				case "READ_MODEL" -> handleReadModelCommand(request);
				default -> unsupported(request);
			};
			GrpcResponseSupport.complete(responseObserver, response);
		} catch (Exception ex) {
			log.warn("gRPC write command failed", ex);
			GrpcResponseSupport.complete(responseObserver, GrpcWriteCommandResponse.newBuilder()
			                                                                       .setSuccess(false)
			                                                                       .setMessage(GrpcResponseSupport.message(ex))
			                                                                       .build());
		}
	}

	private String normalize(String value) {
		return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
	}

	private GrpcWriteCommandResponse handleOrderCommand(GrpcWriteCommandRequest request) throws Exception {
		String action = normalize(request.getAction());
		JsonNode payload = payload(request.getPayloadJson());
		return switch (action) {
			case "ADD_CART", "CREATE_CART" -> {
				OrderCreateRequest orderCreateRequest = payload.isMissingNode() || payload.isEmpty()
				                                        ? null
				                                        : objectMapper.treeToValue(payload, OrderCreateRequest.class);
				OrderDTO order = orderMapper.entityToDTO(orderService.addOrder(orderCreateRequest, request.getActorId()));
				yield success("messages.orders.created", objectMapper.writeValueAsString(order));
			}
			case "CONFIRM" -> {
				OrderConfirmRequest confirmRequest = objectMapper.treeToValue(payload, OrderConfirmRequest.class);
				Boolean confirmed = orderService.confirmOrder(confirmRequest, request.getActorId());
				yield success("messages.orders.updated", objectMapper.writeValueAsString(confirmed));
			}
			case "UPDATE" -> {
				Long id = requiredLong(payload, "id");
				OrderUpdateRequest updateRequest = objectMapper.treeToValue(payload.required("request"),
				                                                            OrderUpdateRequest.class);
				orderService.updateOrder(id, updateRequest);
				yield success("messages.orders.updated", "{}");
			}
			case "DELETE" -> {
				Long id = requiredLong(payload, "id");
				orderService.deleteOrder(id);
				yield success("messages.orders.deleted", "{}");
			}
			default -> unsupported(request);
		};
	}

	private GrpcWriteCommandResponse handleReadModelCommand(GrpcWriteCommandRequest request) throws Exception {
		JsonNode payload = payload(request.getPayloadJson());
		ReadModelType modelType = ReadModelType.valueOf(payload.required("model").asText().toUpperCase(Locale.ROOT));
		Long entityId = requiredLong(payload, "entityId");
		return switch (normalize(request.getAction())) {
			case "SYNC" -> {
				readModelSyncService.sync(modelType, entityId);
				yield success("messages.read.synced", "{}");
			}
			case "REMOVE" -> {
				readModelSyncService.remove(modelType, entityId);
				yield success("messages.read.removed", "{}");
			}
			default -> unsupported(request);
		};
	}

	private GrpcWriteCommandResponse unsupported(GrpcWriteCommandRequest request) {
		return GrpcWriteCommandResponse.newBuilder()
		                               .setSuccess(false)
		                               .setMessage("Unsupported command " + request.getAggregate() + "." + request.getAction())
		                               .build();
	}

	private JsonNode payload(String payloadJson) throws Exception {
		if (payloadJson == null || payloadJson.isBlank()) {
			return objectMapper.missingNode();
		}
		return objectMapper.readTree(payloadJson);
	}

	private GrpcWriteCommandResponse success(String message, String resultJson) {
		return GrpcWriteCommandResponse.newBuilder()
		                               .setSuccess(true)
		                               .setMessage(message)
		                               .setResultJson(resultJson)
		                               .build();
	}

	private Long requiredLong(JsonNode payload, String field) {
		JsonNode node = payload.required(field);
		if (!node.canConvertToLong()) {
			throw new IllegalArgumentException(field + " must be a number");
		}
		return node.asLong();
	}
}
