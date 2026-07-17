package vn.vti.clothing_shop.modules.grpc;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "microservices.grpc.server", name = "enabled", havingValue = "true", matchIfMissing = true)
public class GrpcServerLifecycle implements SmartLifecycle {
	private final List<BindableService> services;

	@Value("${microservices.grpc.server.port:9091}")
	private int port;

	private Server server;
	private boolean running;

	/** Starts value. */
	@Override
	public void start() {
		if (running) {
			return;
		}
		try {
			ServerBuilder<?> builder = ServerBuilder.forPort(port);
			services.forEach(builder::addService);
			builder.addService(ProtoReflectionService.newInstance());
			server = builder.build().start();
			running = true;
			log.info("gRPC module server started on port {} with {} module service(s)", port, services.size());
		} catch (IOException ex) {
			throw new IllegalStateException("Cannot start gRPC module server on port " + port, ex);
		}
	}

	/** Stops value. */
	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

	/** Stops value. */
	@Override
	public void stop() {
		if (server != null) {
			server.shutdown();
		}
		running = false;
	}

	/** Checks whether running. */
	@Override
	public boolean isRunning() {
		return running;
	}

	/** Checks whether auto startup. */
	@Override
	public boolean isAutoStartup() {
		return true;
	}

	/** Gets phase. */
	@Override
	public int getPhase() {
		return Integer.MAX_VALUE;
	}
}
