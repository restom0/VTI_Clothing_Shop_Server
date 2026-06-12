package vn.vti.clothing_shop.mappers;

import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import vn.vti.clothing_shop.utils.TimeUtils;

@Mapper(componentModel = "spring")
public interface DateMapper {
	default Long toEpochMillis(LocalDateTime value) {
		return TimeUtils.toEpochMillis(value);
	}

	default Long toEpochMillis(LocalDate value) {
		return TimeUtils.toEpochMillis(value);
	}

	default LocalDateTime toLocalDateTime(Long value) {
		return TimeUtils.toLocalDateTime(value);
	}

	default LocalDate toLocalDate(Long value) {
		return TimeUtils.toLocalDate(TimeUtils.fromEpochMillis(value));
	}
}
