package vn.vti.clothing_shop.configs;

import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.data.redis.RedisConnectionFailureException;

import static org.assertj.core.api.Assertions.assertThatCode;

class LoggingCacheErrorHandlerTest {
	private final LoggingCacheErrorHandler handler = new LoggingCacheErrorHandler();
	private final ConcurrentMapCache cache = new ConcurrentMapCache("brands");

	@Test
	void shouldSwallowEveryCacheFailureSoRequestsFallBackToTheDataSource() {
		RedisConnectionFailureException failure = new RedisConnectionFailureException("valkey down");

		assertThatCode(() -> handler.handleCacheGetError(failure, cache, "all")).doesNotThrowAnyException();
		assertThatCode(() -> handler.handleCachePutError(failure, cache, "all", "value")).doesNotThrowAnyException();
		assertThatCode(() -> handler.handleCacheEvictError(failure, cache, "all")).doesNotThrowAnyException();
		assertThatCode(() -> handler.handleCacheClearError(failure, cache)).doesNotThrowAnyException();
	}
}
