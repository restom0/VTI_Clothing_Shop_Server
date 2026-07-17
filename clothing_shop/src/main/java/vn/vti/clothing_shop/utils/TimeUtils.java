package vn.vti.clothing_shop.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;

public final class TimeUtils {
	private static volatile ZoneId zoneId = ZoneOffset.UTC;

	/** Creates TimeUtils instance. */
	private TimeUtils() {
	}

	/** Sets zone id. */
	public static void setZoneId(ZoneId configuredZoneId) {
		zoneId = Objects.requireNonNull(configuredZoneId, "configuredZoneId");
	}

	/** Handles zone id. */
	public static ZoneId zoneId() {
		return zoneId;
	}

	/** Returns value. */
	public static OffsetDateTime now() {
		return OffsetDateTime.now(zoneId);
	}

	/** Handles current epoch millis. */
	public static Long currentEpochMillis() {
		return Instant.now().toEpochMilli();
	}

	/** Returns value. */
	public static LocalDate today() {
		return LocalDate.now(zoneId);
	}

	/** Starts of day. */
	public static OffsetDateTime startOfDay(LocalDate value) {
		if (value == null) {
			return null;
		}
		return value.atStartOfDay(zoneId).toOffsetDateTime();
	}

	/** Converts epoch millis. */
	public static Long toEpochMillis(LocalDateTime value) {
		if (value == null) {
			return null;
		}
		return value.atZone(zoneId).toInstant().toEpochMilli();
	}

	/** Converts local date. */
	public static LocalDate toLocalDate(OffsetDateTime value) {
		if (value == null) {
			return null;
		}
		return value.atZoneSameInstant(zoneId).toLocalDate();
	}

	/** Converts epoch millis. */
	public static Long toEpochMillis(OffsetDateTime value) {
		if (value == null) {
			return null;
		}
		return value.toInstant().toEpochMilli();
	}

	/** Converts epoch millis. */
	public static Long toEpochMillis(LocalDate value) {
		return toEpochMillis(startOfDay(value));
	}

	/** Converts local date time. */
	public static LocalDateTime toLocalDateTime(Long value) {
		if (value == null) {
			return null;
		}
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), zoneId);
	}

	/** Converts epoch millis. */
	public static OffsetDateTime fromEpochMillis(Long value) {
		if (value == null) {
			return null;
		}
		return OffsetDateTime.ofInstant(Instant.ofEpochMilli(value), zoneId);
	}
}
