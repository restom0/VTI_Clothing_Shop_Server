package vn.vti.clothing_shop.responses;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import vn.vti.clothing_shop.exceptions.RateLimitExceededException;
import vn.vti.clothing_shop.middlewares.RateLimitInterceptor;
import vn.vti.clothing_shop.services.RateLimitService;

import java.lang.reflect.Method;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
	private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

	@Test
	void handlesMethodArgumentValidationErrors() throws Exception {
		BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
		bindingResult.addError(new FieldError("request", "name", "required"));
		Method method = GlobalExceptionHandlerTest.class.getDeclaredMethod("dummy", String.class);
		MethodArgumentNotValidException exception = new MethodArgumentNotValidException(new MethodParameter(method, 0),
		                                                                               bindingResult);

		var response = handler.handleValidationExceptions(exception);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((ExceptionMessageResponse) response.getBody()).message).isEqualTo("name: required");
	}

	@Test
	void handlesMissingParameterAndConstraintViolations() {
		var missing = handler.handleMissingServletRequestParameterException(
				new MissingServletRequestParameterException("page", "Integer"));
		assertThat(missing.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((ExceptionMessageResponse) missing.getBody()).message).contains("page");

		ConstraintViolation<?> violation = mock(ConstraintViolation.class);
		Path path = mock(Path.class);
		when(path.toString()).thenReturn("size");
		when(violation.getPropertyPath()).thenReturn(path);
		when(violation.getMessage()).thenReturn("must be positive");

		var constraint = handler.handleConstraintViolationException(new ConstraintViolationException(Set.of(violation)));
		assertThat(constraint.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((ExceptionMessageResponse) constraint.getBody()).message).isEqualTo("size: must be positive");
	}

	@Test
	void handlesIllegalArgumentUncheckedAndRateLimit() {
		var illegal = handler.handleIllegalArgumentException(new IllegalArgumentException("bad id"));
		assertThat(illegal.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(((ExceptionMessageResponse) illegal.getBody()).message).contains("bad id");

		var unchecked = handler.handleUncheckedException(new RuntimeException("boom"));
		assertThat(unchecked.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

		RateLimitService.RateLimitDecision decision = new RateLimitService.RateLimitDecision(true, false, "auth", 2, 0,
		                                                                                     5, 60);
		var rateLimit = handler.handleRateLimitExceededException(new RateLimitExceededException(decision));
		assertThat(rateLimit.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
		assertThat(rateLimit.getHeaders().getFirst(RateLimitInterceptor.HEADER_LIMIT)).isEqualTo("2");
		assertThat(rateLimit.getHeaders().getFirst(RateLimitInterceptor.HEADER_REMAINING)).isEqualTo("0");
		assertThat(rateLimit.getHeaders().getFirst(RateLimitInterceptor.HEADER_RESET)).isEqualTo("60");
		assertThat(rateLimit.getHeaders().getFirst(RateLimitInterceptor.HEADER_POLICY)).isEqualTo("auth");
		assertThat(rateLimit.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo("5");
	}

	@SuppressWarnings("unused")
	private static void dummy(String value) {
	}
}
