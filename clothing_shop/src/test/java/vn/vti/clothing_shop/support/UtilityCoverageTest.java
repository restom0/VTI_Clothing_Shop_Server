package vn.vti.clothing_shop.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import vn.vti.clothing_shop.constants.CommentStatus;
import vn.vti.clothing_shop.constants.Messages;
import vn.vti.clothing_shop.constants.PaymentMethod;
import vn.vti.clothing_shop.constants.RegularExpression;
import vn.vti.clothing_shop.constants.StarMin;
import vn.vti.clothing_shop.converters.PaymentMethodConverter;
import vn.vti.clothing_shop.dtos.ins.DateRange;
import vn.vti.clothing_shop.dtos.ins.UserUpdatePasswordRequest;
import vn.vti.clothing_shop.exceptions.BadRequestException;
import vn.vti.clothing_shop.exceptions.WrapperException;
import vn.vti.clothing_shop.mappers.DateMapper;
import vn.vti.clothing_shop.modules.grpc.GrpcResponseSupport;
import vn.vti.clothing_shop.responses.BaseMessageResponse;
import vn.vti.clothing_shop.responses.ExceptionMessageResponse;
import vn.vti.clothing_shop.responses.ResponseHandler;
import vn.vti.clothing_shop.responses.SuccessMessageResponse;
import vn.vti.clothing_shop.responses.SuccessMessageResponseWithMetadata;
import vn.vti.clothing_shop.utils.TimeUtils;
import vn.vti.clothing_shop.validators.EndDateAfterStartDateValidator;
import vn.vti.clothing_shop.validators.HexCodeValidator;
import vn.vti.clothing_shop.validators.NewPasswordNotEqualOldPasswordValidator;
import vn.vti.clothing_shop.validators.PhoneNumberValidator;

import java.lang.reflect.Constructor;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UtilityCoverageTest {
	private final DateMapper dateMapper = new DateMapper() {
	};

	@AfterEach
	void resetZoneId() {
		TimeUtils.setZoneId(ZoneOffset.UTC);
	}

	@Test
	void coversEnumsAndConverters() {
		assertThat(CommentStatus.values()).containsExactly(CommentStatus.SUSPENDED, CommentStatus.APPROVED,
		                                                   CommentStatus.ONHOLD);
		assertThat(PaymentMethod.fromValue(null)).isNull();
		assertThat(PaymentMethod.fromValue("COD")).isEqualTo(PaymentMethod.COD);
		assertThat(PaymentMethod.fromValue("EBanking")).isEqualTo(PaymentMethod.E_BANKING);
		assertThat(PaymentMethod.MOMO.getValue()).isEqualTo("MOMO");
		assertThatThrownBy(() -> PaymentMethod.fromValue("UNKNOWN")).isInstanceOf(IllegalArgumentException.class)
		                                                              .hasMessage("Unknown payment method: UNKNOWN");

		PaymentMethodConverter converter = new PaymentMethodConverter();
		assertThat(converter.convertToDatabaseColumn(null)).isNull();
		assertThat(converter.convertToDatabaseColumn(PaymentMethod.E_BANKING)).isEqualTo("EBanking");
		assertThat(converter.convertToEntityAttribute(null)).isNull();
		assertThat(converter.convertToEntityAttribute("ZALO_PAY")).isEqualTo(PaymentMethod.ZALO_PAY);
	}

	@Test
	void coversTimeAndDateMapperHelpers() {
		TimeUtils.setZoneId(ZoneOffset.UTC);
		LocalDate date = LocalDate.of(2026, 7, 16);
		LocalDateTime dateTime = LocalDateTime.of(2026, 7, 16, 9, 30);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, ZoneOffset.UTC);
		long expectedDateMillis = Instant.parse("2026-07-16T00:00:00Z").toEpochMilli();
		long expectedDateTimeMillis = Instant.parse("2026-07-16T09:30:00Z").toEpochMilli();

		assertThat(TimeUtils.zoneId()).isEqualTo(ZoneOffset.UTC);
		assertThat(TimeUtils.today()).isNotNull();
		assertThat(TimeUtils.now()).isNotNull();
		assertThat(TimeUtils.currentEpochMillis()).isPositive();
		assertThat(TimeUtils.startOfDay(null)).isNull();
		assertThat(TimeUtils.startOfDay(date)).isEqualTo(OffsetDateTime.parse("2026-07-16T00:00:00Z"));
		assertThat(TimeUtils.toEpochMillis((LocalDateTime) null)).isNull();
		assertThat(TimeUtils.toEpochMillis(dateTime)).isEqualTo(expectedDateTimeMillis);
		assertThat(TimeUtils.toEpochMillis((LocalDate) null)).isNull();
		assertThat(TimeUtils.toEpochMillis(date)).isEqualTo(expectedDateMillis);
		assertThat(TimeUtils.toEpochMillis((OffsetDateTime) null)).isNull();
		assertThat(TimeUtils.toEpochMillis(offsetDateTime)).isEqualTo(expectedDateTimeMillis);
		assertThat(TimeUtils.toLocalDate(null)).isNull();
		assertThat(TimeUtils.toLocalDate(offsetDateTime)).isEqualTo(date);
		assertThat(TimeUtils.toLocalDateTime(null)).isNull();
		assertThat(TimeUtils.toLocalDateTime(expectedDateTimeMillis)).isEqualTo(dateTime);
		assertThat(TimeUtils.fromEpochMillis(null)).isNull();
		assertThat(TimeUtils.fromEpochMillis(expectedDateTimeMillis)).isEqualTo(offsetDateTime);
		assertThatNullPointerException().isThrownBy(() -> TimeUtils.setZoneId(null));

		assertThat(dateMapper.toEpochMillis(dateTime)).isEqualTo(expectedDateTimeMillis);
		assertThat(dateMapper.toEpochMillis(date)).isEqualTo(expectedDateMillis);
		assertThat(dateMapper.toLocalDateTime(expectedDateTimeMillis)).isEqualTo(dateTime);
		assertThat(dateMapper.toLocalDate(expectedDateMillis)).isEqualTo(date);
		assertThat(dateMapper.toLocalDate(null)).isNull();
	}

	@Test
	void coversValidators() {
		EndDateAfterStartDateValidator dateValidator = new EndDateAfterStartDateValidator();
		assertThat(dateValidator.isValid(range(null, LocalDate.now()), null)).isTrue();
		assertThat(dateValidator.isValid(range(LocalDate.now(), null), null)).isTrue();
		assertThat(dateValidator.isValid(range(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 1)), null)).isTrue();
		assertThat(dateValidator.isValid(range(LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 1)), null)).isFalse();

		NewPasswordNotEqualOldPasswordValidator passwordValidator = new NewPasswordNotEqualOldPasswordValidator();
		assertThat(passwordValidator.isValid(null, null)).isTrue();
		assertThat(passwordValidator.isValid(new UserUpdatePasswordRequest(null, "new", 1L), null)).isTrue();
		assertThat(passwordValidator.isValid(new UserUpdatePasswordRequest("old", null, 1L), null)).isTrue();
		assertThat(passwordValidator.isValid(new UserUpdatePasswordRequest("same", "same", 1L), null)).isFalse();
		assertThat(passwordValidator.isValid(new UserUpdatePasswordRequest("old", "new", 1L), null)).isTrue();

		HexCodeValidator hexCodeValidator = new HexCodeValidator();
		assertThat(hexCodeValidator.isValid("#A1b2C3", null)).isTrue();
		assertThat(hexCodeValidator.isValid(null, null)).isFalse();
		assertThat(hexCodeValidator.isValid("blue", null)).isFalse();

		PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();
		assertThat(phoneNumberValidator.isValid("+84 912 345 678", null)).isTrue();
		assertThat(phoneNumberValidator.isValid(null, null)).isFalse();
		assertThat(phoneNumberValidator.isValid("abc", null)).isFalse();
		assertThat(phoneNumberValidator.isValid("12345678", null)).isFalse();
		assertThat(phoneNumberValidator.isValid("1234567890123456", null)).isFalse();
	}

	@Test
	void coversResponseBuildersAndGrpcSupport() {
		ResponseEntity<BaseMessageResponse> success = ResponseHandler.successBuilder(HttpStatus.CREATED, "raw-data");
		assertThat(success.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(success.getBody()).isInstanceOf(SuccessMessageResponse.class);
		assertThat(((SuccessMessageResponse) success.getBody()).data).isEqualTo("raw-data");

		ResponseEntity<BaseMessageResponse> successWithMetadata = ResponseHandler.successBuilder(HttpStatus.OK,
		                                                                                         List.of("item"),
		                                                                                         Map.of("page", 1));
		assertThat(successWithMetadata.getBody()).isInstanceOf(SuccessMessageResponseWithMetadata.class);
		SuccessMessageResponseWithMetadata body = (SuccessMessageResponseWithMetadata) successWithMetadata.getBody();
		assertThat(body.data).isEqualTo(List.of("item"));
		assertThat(body.metadata).isEqualTo(Map.of("page", 1));

		ResponseEntity<BaseMessageResponse> exception = ResponseHandler.exceptionBuilder("problem", HttpStatus.CONFLICT);
		assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertThat(((ExceptionMessageResponse) exception.getBody()).message).isEqualTo("problem");

		ResponseEntity<BaseMessageResponse> wrapped = ResponseHandler.exceptionBuilder(
				new WrapperException(new BadRequestException("bad-request")));
		assertThat(wrapped.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((ExceptionMessageResponse) wrapped.getBody()).message).isEqualTo("bad-request");

		assertThat(GrpcResponseSupport.message(new IllegalArgumentException("bad"))).isEqualTo("bad");
		assertThat(GrpcResponseSupport.message(new IllegalArgumentException())).isEqualTo("messages.request.unexpected");
	}

	@Test
	void utilityConstructorsStayClosed() throws Exception {
		assertUtilityConstructorThrows(Messages.class);
		assertUtilityConstructorThrows(RegularExpression.class);
		assertUtilityConstructorThrows(StarMin.class);
		assertUtilityConstructorThrows(ResponseHandler.class);
		assertThat(Messages.MESSAGE_ORDER_NOTFOUND).isEqualTo("messages.orders.notfound");
		assertThat(RegularExpression.EMAIL.matcher("ada@example.com").matches()).isTrue();
		assertThat(StarMin.STAR_MIN).isEqualTo(2.5F);
	}

	private static DateRange range(LocalDate start, LocalDate end) {
		return new DateRange() {
			@Override
			public LocalDate availableDate() {
				return start;
			}

			@Override
			public LocalDate endDate() {
				return end;
			}
		};
	}

	private static void assertUtilityConstructorThrows(Class<?> type) throws Exception {
		Constructor<?> constructor = type.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertThatThrownBy(constructor::newInstance).hasCauseInstanceOf(IllegalStateException.class);
	}
}
