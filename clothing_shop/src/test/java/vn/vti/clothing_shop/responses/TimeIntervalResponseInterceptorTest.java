package vn.vti.clothing_shop.responses;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TimeIntervalResponseInterceptorTest {
	private final TimeIntervalFormatter formatter = new TimeIntervalFormatter();
	private final TimeIntervalResponseInterceptor interceptor = new TimeIntervalResponseInterceptor(formatter);

	@BeforeEach
	void setUp() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("labels/labels");
		messageSource.setDefaultEncoding("UTF-8");
		new MessageResolver(messageSource);
	}

	@AfterEach
	void tearDown() {
		TimeIntervalContext.clear();
	}

	@Test
	void shouldRenderTemporalFieldsAsLocalizedIntervalsWhenLanguageCodeIsEnabled() {
		TimeIntervalContext.enable(Locale.ENGLISH);
		long createdAt = Instant.now().minus(Duration.ofMinutes(5)).toEpochMilli();
		SuccessMessageResponse response = new SuccessMessageResponse(200, Map.of("createdAt", createdAt, "id", 10L));

		Object transformed = interceptor.beforeBodyWrite(
				response,
				null,
				MediaType.APPLICATION_JSON,
				null,
				null,
				null
		);

		assertThat(transformed).isInstanceOf(Map.class);
		Map<?, ?> body = (Map<?, ?>) transformed;
		Map<?, ?> data = (Map<?, ?>) body.get("data");
		assertThat(data.get("createdAt")).isEqualTo("5 minutes ago");
		assertThat(data.get("id")).isEqualTo(10L);
	}

	@Test
	void shouldLeaveBodyUntouchedWhenLanguageCodeIsNotEnabled() {
		SuccessMessageResponse response = new SuccessMessageResponse(200, Map.of("createdAt", 10L));

		Object transformed = interceptor.beforeBodyWrite(
				response,
				null,
				MediaType.APPLICATION_JSON,
				null,
				null,
				null
		);

		assertThat(transformed).isSameAs(response);
	}
}
