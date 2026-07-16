package vn.vti.clothing_shop.modules.grpc;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class GrpcServerLifecycleTest {
	@Test
	void startsOnceAndStopsWithCallback() {
		GrpcServerLifecycle lifecycle = new GrpcServerLifecycle(List.of());
		ReflectionTestUtils.setField(lifecycle, "port", 0);

		lifecycle.start();
		lifecycle.start();
		assertThat(lifecycle.isRunning()).isTrue();
		assertThat(lifecycle.isAutoStartup()).isTrue();
		assertThat(lifecycle.getPhase()).isEqualTo(Integer.MAX_VALUE);

		AtomicBoolean callbackInvoked = new AtomicBoolean();
		lifecycle.stop(() -> callbackInvoked.set(true));

		assertThat(callbackInvoked).isTrue();
		assertThat(lifecycle.isRunning()).isFalse();
	}
}
