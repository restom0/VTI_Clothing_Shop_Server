package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface DateMapper {
	default Long toEpochMillis(LocalDateTime value) {
		if (value == null) {
			return null;
		}
		return value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	default Long toEpochMillis(LocalDate value) {
		if (value == null) {
			return null;
		}
		return value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	default LocalDateTime toLocalDateTime(Long value) {
		if (value == null) {
			return null;
		}
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault());
	}

	default LocalDate toLocalDate(Long value) {
		if (value == null) {
			return null;
		}
		return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
