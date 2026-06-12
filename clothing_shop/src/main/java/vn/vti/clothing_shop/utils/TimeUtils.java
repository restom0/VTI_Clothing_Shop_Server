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

	private TimeUtils() {
	}

	public static void setZoneId(ZoneId configuredZoneId) {
		zoneId = Objects.requireNonNull(configuredZoneId, "configuredZoneId");
	}

	public static ZoneId zoneId() {
		return zoneId;
	}

	public static OffsetDateTime now() {
		return OffsetDateTime.now(zoneId);
	}

	public static Long currentEpochMillis() {
		return Instant.now().toEpochMilli();
	}

	public static LocalDate today() {
		return LocalDate.now(zoneId);
	}

	public static OffsetDateTime startOfDay(LocalDate value) {
		if (value == null) {
			return null;
		}
		return value.atStartOfDay(zoneId).toOffsetDateTime();
	}

	public static Long toEpochMillis(LocalDateTime value) {
		if (value == null) {
			return null;
		}
		return value.atZone(zoneId).toInstant().toEpochMilli();
	}

	public static LocalDate toLocalDate(OffsetDateTime value) {
		if (value == null) {
			return null;
		}
		return value.atZoneSameInstant(zoneId).toLocalDate();
	}

	public static Long toEpochMillis(OffsetDateTime value) {
		if (value == null) {
			return null;
		}
		return value.toInstant().toEpochMilli();
	}

	public static Long toEpochMillis(LocalDate value) {
		return toEpochMillis(startOfDay(value));
	}

	public static LocalDateTime toLocalDateTime(Long value) {
		if (value == null) {
			return null;
		}
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), zoneId);
	}

	public static OffsetDateTime fromEpochMillis(Long value) {
		if (value == null) {
			return null;
		}
		return OffsetDateTime.ofInstant(Instant.ofEpochMilli(value), zoneId);
	}
}
