package vn.vti.clothing_shop.responses;

import org.springframework.stereotype.Component;

import vn.vti.clothing_shop.utils.TimeUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Locale;

@Component
public class TimeIntervalFormatter {
	private static final long SECONDS_PER_MINUTE = 60;
	private static final long MINUTES_PER_HOUR = 60;
	private static final long HOURS_PER_DAY = 24;
	private static final long DAYS_PER_MONTH = 30;
	private static final long DAYS_PER_YEAR = 365;

	/** Handles format temporal. */
	public Object formatTemporal(Object value, Locale locale) {
		if (value instanceof Long epochMillis) {
			return format(epochMillis, locale);
		}
		if (value instanceof LocalDateTime dateTime) {
			return format(TimeUtils.toEpochMillis(dateTime), locale);
		}
		if (value instanceof LocalDate date) {
			return format(TimeUtils.toEpochMillis(date), locale);
		}
		if (value instanceof OffsetDateTime dateTime) {
			return format(TimeUtils.toEpochMillis(dateTime), locale);
		}
		if (value instanceof Instant instant) {
			return format(instant.toEpochMilli(), locale);
		}
		return value;
	}

	/** Handles format. */
	public String format(Long epochMillis, Locale locale) {
		if (epochMillis == null) {
			return null;
		}
		long deltaSeconds = Duration.between(Instant.ofEpochMilli(epochMillis), Instant.now()).toSeconds();
		if (Math.abs(deltaSeconds) < 45) {
			return MessageResolver.resolve(locale, "messages.time.now");
		}

		boolean future = deltaSeconds < 0;
		long absoluteSeconds = Math.abs(deltaSeconds);
		IntervalUnit unit = resolveUnit(absoluteSeconds);
		String plurality = unit.amount == 1 ? "one" : "many";
		String direction = future ? "future" : "past";
		String key = "messages.time." + direction + "." + unit.name + "." + plurality;
		return MessageResolver.resolve(locale, key, unit.amount);
	}

	/** Resolves unit. */
	private IntervalUnit resolveUnit(long seconds) {
		long minutes = seconds / SECONDS_PER_MINUTE;
		if (minutes < 1) {
			return new IntervalUnit("second", seconds);
		}
		long hours = minutes / MINUTES_PER_HOUR;
		if (hours < 1) {
			return new IntervalUnit("minute", minutes);
		}
		long days = hours / HOURS_PER_DAY;
		if (days < 1) {
			return new IntervalUnit("hour", hours);
		}
		if (days < DAYS_PER_MONTH) {
			return new IntervalUnit("day", days);
		}
		if (days < DAYS_PER_YEAR) {
			return new IntervalUnit("month", days / DAYS_PER_MONTH);
		}
		return new IntervalUnit("year", days / DAYS_PER_YEAR);
	}

	/** Creates IntervalUnit instance. */
	private record IntervalUnit(String name, long amount) {
	}
}
