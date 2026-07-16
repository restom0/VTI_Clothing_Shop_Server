package vn.vti.clothing_shop.configs;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoggingConfigTest {
	private final LoggingConfig loggingConfig = new LoggingConfig();

	@Test
	void pointcutMethodIsCallableAndUncheckedExceptionsAreLogged() {
		JoinPoint joinPoint = mock(JoinPoint.class);
		Signature signature = mock(Signature.class);
		when(joinPoint.getSignature()).thenReturn(signature);
		when(signature.toShortString()).thenReturn("BrandService.create(..)");

		assertThatCode(loggingConfig::applicationLayer).doesNotThrowAnyException();
		assertThatCode(() -> loggingConfig.logUncheckedException(joinPoint, new IllegalStateException("boom")))
				.doesNotThrowAnyException();

		verify(signature).toShortString();
	}
}
