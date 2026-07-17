package vn.vti.clothing_shop.modules.payment;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.dtos.outs.OrderDTO;
import vn.vti.clothing_shop.grpc.GrpcPaymentCheckoutRequest;
import vn.vti.clothing_shop.grpc.GrpcPaymentCheckoutResponse;
import vn.vti.clothing_shop.grpc.PaymentModuleServiceGrpc;
import vn.vti.clothing_shop.modules.grpc.GrpcResponseSupport;
import vn.vti.clothing_shop.services.interfaces.PaymentService;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "microservices.modules.payment", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PaymentModuleGrpcService extends PaymentModuleServiceGrpc.PaymentModuleServiceImplBase {
	private final PaymentService paymentService;
	private final ObjectMapper objectMapper;

	/** Creates checkout. */
	@Override
	public void createCheckout(GrpcPaymentCheckoutRequest request, StreamObserver<GrpcPaymentCheckoutResponse> responseObserver) {
		try {
			OrderDTO orderDTO = objectMapper.readValue(request.getOrderJson(), OrderDTO.class);
			vn.vti.clothing_shop.dtos.outs.PaymentCheckoutResponse checkout = paymentService.createCheckout(orderDTO);
			GrpcResponseSupport.complete(responseObserver, GrpcPaymentCheckoutResponse.newBuilder()
			                                                                          .setSuccess(true)
			                                                                          .setMessage(
					                                                                          "messages.orders.paymentLinkCreated")
			                                                                          .setCheckoutJson(
					                                                                          objectMapper.writeValueAsString(
							                                                                          checkout))
			                                                                          .build());
		} catch (Exception ex) {
			log.warn("gRPC payment checkout failed", ex);
			GrpcResponseSupport.complete(responseObserver, GrpcPaymentCheckoutResponse.newBuilder()
			                                                                          .setSuccess(false)
			                                                                          .setMessage(GrpcResponseSupport.message(ex))
			                                                                          .build());
		}
	}
}
