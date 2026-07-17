package vn.vti.clothing_shop.responses;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;

import vn.vti.clothing_shop.utils.TimeUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.List;
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

	@Test
	void supportsEveryResponseShape() {
		assertThat(interceptor.supports(null, null)).isTrue();
	}

	@Test
	void shouldLeaveBodyUntouchedWhenBodyIsNullNonJsonOrNotBaseResponse() {
		TimeIntervalContext.enable(Locale.ENGLISH);
		SuccessMessageResponse response = new SuccessMessageResponse(200, Map.of("createdAt", 10L));
		Map<String, Object> plainBody = Map.of("createdAt", 10L);

		assertThat(interceptor.beforeBodyWrite(null, null, MediaType.APPLICATION_JSON, null, null, null)).isNull();
		assertThat(interceptor.beforeBodyWrite(response, null, MediaType.TEXT_PLAIN, null, null, null)).isSameAs(response);
		assertThat(interceptor.beforeBodyWrite(plainBody, null, MediaType.APPLICATION_JSON, null, null, null))
				.isSameAs(plainBody);
	}

	@Test
	void shouldTransformNestedCollectionsArraysObjectsAndCircularReferences() {
		TimeIntervalContext.enable(Locale.ENGLISH);
		long deletedAt = Instant.now().minus(Duration.ofMinutes(10)).toEpochMilli();
		LoopPayload loopPayload = new LoopPayload(Instant.now().minus(Duration.ofMinutes(2)));
		loopPayload.child = loopPayload;
		BeanPayload beanPayload = new BeanPayload(LocalDateTime.now(TimeUtils.zoneId()).minusHours(3));
		SuccessMessageResponse response = new SuccessMessageResponse(200, Map.of(
				"deletedAt", deletedAt,
				"items", List.of(loopPayload),
				"history", new Object[] { Map.of("updatedAt", Instant.now().minus(Duration.ofMinutes(4))) },
				"bean", beanPayload
		));

		Object transformed = interceptor.beforeBodyWrite(
				response,
				null,
				MediaType.valueOf("application/vnd.api+json"),
				null,
				null,
				null
		);

		Map<?, ?> body = (Map<?, ?>) transformed;
		Map<?, ?> data = (Map<?, ?>) body.get("data");
		assertThat(data.get("deletedAt")).isEqualTo("10 minutes ago");
		List<?> items = (List<?>) data.get("items");
		Map<?, ?> loop = (Map<?, ?>) items.get(0);
		assertThat(loop.get("createdAt")).isEqualTo("2 minutes ago");
		assertThat(loop.get("child")).isNull();
		List<?> history = (List<?>) data.get("history");
		Map<?, ?> historyItem = (Map<?, ?>) history.get(0);
		assertThat(historyItem.get("updatedAt")).isEqualTo("4 minutes ago");
		Map<?, ?> bean = (Map<?, ?>) data.get("bean");
		assertThat(bean.get("startDate")).isEqualTo("3 hours ago");
		assertThat(bean.get("name")).isEqualTo("bean");
		assertThat(bean.get("exploding")).isNull();
	}

	private static final class LoopPayload {
		public final Instant createdAt;
		public LoopPayload child;

		private LoopPayload(Instant createdAt) {
			this.createdAt = createdAt;
		}
	}

	private static final class BeanPayload {
		private final LocalDateTime startDate;

		private BeanPayload(LocalDateTime startDate) {
			this.startDate = startDate;
		}

		public LocalDateTime getStartDate() {
			return startDate;
		}

		public String getName() {
			return "bean";
		}

		public String getExploding() {
			throw new IllegalStateException("boom");
		}
	}
}
