package vn.vti.clothing_shop.modules.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import vn.vti.clothing_shop.grpc.AuthModuleServiceGrpc;
import vn.vti.clothing_shop.grpc.PaymentModuleServiceGrpc;
import vn.vti.clothing_shop.grpc.ReadModuleServiceGrpc;
import vn.vti.clothing_shop.grpc.WriteModuleServiceGrpc;

@Configuration
public class GrpcClientConfiguration {

	/** Handles auth gRPC channel. */
	@Bean(destroyMethod = "shutdownNow")
	public ManagedChannel authGrpcChannel(@Value("${microservices.grpc.client.auth-target:localhost:9091}") String target) {
		return channel(target);
	}

	/** Handles channel. */
	private ManagedChannel channel(String target) {
		return ManagedChannelBuilder.forTarget(target)
		                            .usePlaintext()
		                            .build();
	}

	/** Handles payment gRPC channel. */
	@Bean(destroyMethod = "shutdownNow")
	public ManagedChannel paymentGrpcChannel(@Value("${microservices.grpc.client.payment-target:localhost:9091}") String target) {
		return channel(target);
	}

	/** Handles read gRPC channel. */
	@Bean(destroyMethod = "shutdownNow")
	public ManagedChannel readGrpcChannel(@Value("${microservices.grpc.client.read-target:localhost:9091}") String target) {
		return channel(target);
	}

	/** Handles write gRPC channel. */
	@Bean(destroyMethod = "shutdownNow")
	public ManagedChannel writeGrpcChannel(@Value("${microservices.grpc.client.write-target:localhost:9091}") String target) {
		return channel(target);
	}

	/** Handles auth module gRPC client. */
	@Bean
	public AuthModuleServiceGrpc.AuthModuleServiceBlockingStub authModuleGrpcClient(
			@Qualifier("authGrpcChannel") ManagedChannel channel) {
		return AuthModuleServiceGrpc.newBlockingStub(channel);
	}

	/** Handles payment module gRPC client. */
	@Bean
	public PaymentModuleServiceGrpc.PaymentModuleServiceBlockingStub paymentModuleGrpcClient(
			@Qualifier("paymentGrpcChannel") ManagedChannel channel) {
		return PaymentModuleServiceGrpc.newBlockingStub(channel);
	}

	/** Handles read module gRPC client. */
	@Bean
	public ReadModuleServiceGrpc.ReadModuleServiceBlockingStub readModuleGrpcClient(
			@Qualifier("readGrpcChannel") ManagedChannel channel) {
		return ReadModuleServiceGrpc.newBlockingStub(channel);
	}

	/** Handles write module gRPC client. */
	@Bean
	public WriteModuleServiceGrpc.WriteModuleServiceBlockingStub writeModuleGrpcClient(
			@Qualifier("writeGrpcChannel") ManagedChannel channel) {
		return WriteModuleServiceGrpc.newBlockingStub(channel);
	}
}
