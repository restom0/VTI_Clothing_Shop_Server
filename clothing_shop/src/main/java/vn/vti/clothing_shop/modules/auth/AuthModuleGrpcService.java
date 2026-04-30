package vn.vti.clothing_shop.modules.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.grpc.stub.StreamObserver;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import vn.vti.clothing_shop.dtos.ins.UserLoginRequest;
import vn.vti.clothing_shop.dtos.outs.UserLoginDTO;
import vn.vti.clothing_shop.grpc.AuthModuleServiceGrpc;
import vn.vti.clothing_shop.grpc.GrpcAuthLoginRequest;
import vn.vti.clothing_shop.grpc.GrpcAuthLoginResponse;
import vn.vti.clothing_shop.grpc.GrpcValidateTokenRequest;
import vn.vti.clothing_shop.grpc.GrpcValidateTokenResponse;
import vn.vti.clothing_shop.modules.grpc.GrpcResponseSupport;
import vn.vti.clothing_shop.services.JwtService;
import vn.vti.clothing_shop.services.interfaces.UserService;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "microservices.modules.auth", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AuthModuleGrpcService extends AuthModuleServiceGrpc.AuthModuleServiceImplBase {
	private final UserService userService;
	private final JwtService jwtService;
	private final ObjectMapper objectMapper;

	@Override
	public void login(GrpcAuthLoginRequest request, StreamObserver<GrpcAuthLoginResponse> responseObserver) {
		try {
			UserLoginDTO user = userService.getUser(new UserLoginRequest(
					request.getUsernameOrEmailOrPhoneNumber(),
					request.getPassword()
			));
			GrpcResponseSupport.complete(responseObserver, GrpcAuthLoginResponse.newBuilder()
			                                                                    .setSuccess(true)
			                                                                    .setMessage("messages.auth.loginSuccess")
			                                                                    .setToken(user.getToken())
			                                                                    .setUserJson(
					                                                                    objectMapper.writeValueAsString(user))
			                                                                    .build());
		} catch (Exception ex) {
			log.warn("gRPC auth login failed", ex);
			GrpcResponseSupport.complete(responseObserver, GrpcAuthLoginResponse.newBuilder()
			                                                                    .setSuccess(false)
			                                                                    .setMessage(GrpcResponseSupport.message(ex))
			                                                                    .build());
		}
	}

	@Override
	public void validateToken(GrpcValidateTokenRequest request, StreamObserver<GrpcValidateTokenResponse> responseObserver) {
		try {
			String subject = jwtService.extractId(request.getToken());
			Date expiresAt = jwtService.extractClaim(request.getToken(), Claims::getExpiration);
			boolean valid = !jwtService.isTokenExpired(request.getToken());
			GrpcResponseSupport.complete(responseObserver, GrpcValidateTokenResponse.newBuilder()
			                                                                        .setValid(valid)
			                                                                        .setMessage(valid ? "messages.auth.tokenValid"
			                                                                                          : "messages.auth.tokenExpired")
			                                                                        .setSubject(subject)
			                                                                        .setExpiresAtEpochMs(expiresAt.getTime())
			                                                                        .build());
		} catch (Exception ex) {
			log.warn("gRPC token validation failed", ex);
			GrpcResponseSupport.complete(responseObserver, GrpcValidateTokenResponse.newBuilder()
			                                                                        .setValid(false)
			                                                                        .setMessage(GrpcResponseSupport.message(ex))
			                                                                        .build());
		}
	}
}
