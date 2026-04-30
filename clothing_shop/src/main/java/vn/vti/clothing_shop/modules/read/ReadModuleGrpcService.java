package vn.vti.clothing_shop.modules.read;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.vti.clothing_shop.dtos.outs.BrandDTO;
import vn.vti.clothing_shop.dtos.outs.CategoryDTO;
import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.dtos.outs.ProductDTO;
import vn.vti.clothing_shop.dtos.outs.VoucherDTO;
import vn.vti.clothing_shop.grpc.GrpcReadRequest;
import vn.vti.clothing_shop.grpc.GrpcReadResponse;
import vn.vti.clothing_shop.grpc.ReadModuleServiceGrpc;
import vn.vti.clothing_shop.modules.grpc.GrpcResponseSupport;
import vn.vti.clothing_shop.readmodels.ReadModelType;
import vn.vti.clothing_shop.services.impl.MongoReadModelQueryService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "microservices.modules.read", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ReadModuleGrpcService extends ReadModuleServiceGrpc.ReadModuleServiceImplBase {
	private final MongoReadModelQueryService queryService;
	private final ObjectMapper objectMapper;

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void query(GrpcReadRequest request, StreamObserver<GrpcReadResponse> responseObserver) {
		try {
			ReadModelType modelType = ReadModelType.valueOf(request.getModel().trim().toUpperCase(Locale.ROOT));
			Class targetType = targetTypeFor(modelType);
			GrpcReadResponse.Builder response = GrpcReadResponse.newBuilder()
			                                                    .setSuccess(true)
			                                                    .setMessage("messages.read.success");

			if (StringUtils.hasText(request.getLookupKey())) {
				Optional<?> item = queryService.findByLookupKey(modelType, request.getLookupKey(), targetType);
				item.ifPresent(value -> response.setItemJson(toJson(value)));
			} else if (request.getEntityId() > 0 && request.getOwnerId() > 0) {
				Optional<?> item = queryService.findByIdAndOwner(modelType, request.getEntityId(), request.getOwnerId(),
				                                                 targetType);
				item.ifPresent(value -> response.setItemJson(toJson(value)));
			} else if (request.getEntityId() > 0) {
				Optional<?> item = queryService.findById(modelType, request.getEntityId(), targetType);
				item.ifPresent(value -> response.setItemJson(toJson(value)));
			} else {
				List<?> items = request.getOwnerId() > 0
				                ? queryService.findByOwner(modelType, request.getOwnerId(), targetType)
				                : queryService.findAll(modelType, targetType);
				items.stream()
				     .map(this::toJson)
				     .forEach(response::addItemsJson);
			}

			GrpcResponseSupport.complete(responseObserver, response.build());
		} catch (Exception ex) {
			log.warn("gRPC read query failed", ex);
			GrpcResponseSupport.complete(responseObserver, GrpcReadResponse.newBuilder()
			                                                               .setSuccess(false)
			                                                               .setMessage(GrpcResponseSupport.message(ex))
			                                                               .build());
		}
	}

	private Class<?> targetTypeFor(ReadModelType modelType) {
		return switch (modelType) {
			case BRAND -> BrandDTO.class;
			case CATEGORY -> CategoryDTO.class;
			case PRODUCT -> ProductDTO.class;
			case VOUCHER -> VoucherDTO.class;
			case ORDER -> OrderDTO.class;
		};
	}

	private String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (Exception ex) {
			throw new IllegalStateException("Cannot serialize read model response", ex);
		}
	}
}
