package vn.vti.clothing_shop.responses;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class TimeIntervalFormatterTest {
	private final TimeIntervalFormatter formatter = new TimeIntervalFormatter();

	@BeforeEach
	void setUp() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("labels/labels");
		messageSource.setDefaultEncoding("UTF-8");
		new MessageResolver(messageSource);
	}

	@Test
	void formatHandlesNullCurrentAndPastIntervals() {
		assertThat(formatter.format(null, Locale.ENGLISH)).isNull();
		assertThat(formatter.format(Instant.now().toEpochMilli(), Locale.ENGLISH)).isEqualTo("just now");
		assertThat(formatter.format(Instant.now().minusSeconds(50).toEpochMilli(), Locale.ENGLISH))
				.isEqualTo("50 seconds ago");
		assertThat(formatter.format(Instant.now().minusSeconds(90).toEpochMilli(), Locale.ENGLISH))
				.isEqualTo("1 minute ago");
		assertThat(formatter.format(Instant.now().minus(Duration.ofHours(3)).toEpochMilli(), Locale.ENGLISH))
				.isEqualTo("3 hours ago");
		assertThat(formatter.format(Instant.now().minus(Duration.ofDays(3)).toEpochMilli(), Locale.ENGLISH))
				.isEqualTo("3 days ago");
		assertThat(formatter.format(Instant.now().minus(Duration.ofDays(90)).toEpochMilli(), Locale.ENGLISH))
				.isEqualTo("3 months ago");
		assertThat(formatter.format(Instant.now().minus(Duration.ofDays(800)).toEpochMilli(), Locale.ENGLISH))
				.isEqualTo("2 years ago");
	}

	@Test
	void formatHandlesFutureIntervals() {
		assertThat(formatter.format(Instant.now().plusSeconds(90).toEpochMilli(), Locale.ENGLISH))
				.isEqualTo("in 1 minute");
		assertThat(formatter.format(Instant.now().plus(Duration.ofDays(3)).toEpochMilli(), Locale.ENGLISH))
				.isEqualTo("in 3 days");
	}

	@Test
	void formatTemporalSupportsTemporalTypesAndLeavesOtherValuesUntouched() {
		assertThat(formatter.formatTemporal("unchanged", Locale.ENGLISH)).isEqualTo("unchanged");
		assertThat(formatter.formatTemporal(Instant.now().minusSeconds(90), Locale.ENGLISH)).isEqualTo("1 minute ago");
		assertThat(formatter.formatTemporal(LocalDateTime.now(ZoneOffset.UTC).minusHours(2), Locale.ENGLISH))
				.isEqualTo("2 hours ago");
		assertThat(formatter.formatTemporal(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(2), Locale.ENGLISH))
				.isEqualTo("in 2 minutes");
		assertThat(formatter.formatTemporal(LocalDate.now(ZoneOffset.UTC).minusDays(3), Locale.ENGLISH))
				.asString()
				.endsWith("days ago");
	}
}
