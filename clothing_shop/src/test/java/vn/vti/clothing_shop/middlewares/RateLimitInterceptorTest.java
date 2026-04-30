package vn.vti.clothing_shop.middlewares;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import vn.vti.clothing_shop.configs.RateLimitProperties;
import vn.vti.clothing_shop.exceptions.RateLimitExceededException;
import vn.vti.clothing_shop.services.RateLimitService;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateLimitInterceptorTest {

	@Test
	void writesHeadersAndBlocksWhenQuotaIsExceeded() {
		RateLimitProperties properties = new RateLimitProperties();
		properties.getDefaultPolicy().setCapacity(1);
		properties.getDefaultPolicy().setRefillTokens(1);
		properties.getDefaultPolicy().setRefillPeriod(Duration.ofHours(1));
		RateLimitInterceptor interceptor = new RateLimitInterceptor(new RateLimitService(properties));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/product");
		MockHttpServletResponse firstResponse = new MockHttpServletResponse();
		MockHttpServletResponse blockedResponse = new MockHttpServletResponse();

		assertThat(interceptor.preHandle(request, firstResponse, new Object())).isTrue();
		assertThat(firstResponse.getHeader(RateLimitInterceptor.HEADER_LIMIT)).isEqualTo("1");
		assertThat(firstResponse.getHeader(RateLimitInterceptor.HEADER_REMAINING)).isEqualTo("0");

		assertThatThrownBy(() -> interceptor.preHandle(request, blockedResponse, new Object())).isInstanceOf(
				RateLimitExceededException.class);
		assertThat(blockedResponse.getHeader("Retry-After")).isNotBlank();
	}
}
