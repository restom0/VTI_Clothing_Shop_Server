package vn.vti.clothing_shop.responses;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import vn.vti.clothing_shop.exceptions.RateLimitExceededException;
import vn.vti.clothing_shop.middlewares.RateLimitInterceptor;
import vn.vti.clothing_shop.services.RateLimitService;

import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseMessageResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        if (errors.isBlank()) {
            errors = ex.getBindingResult().getAllErrors().stream()
                    .map(error -> MessageResolver.resolve(error.getDefaultMessage()))
                    .collect(Collectors.joining("; "));
        }
        return ResponseHandler.exceptionBuilder(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseMessageResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = MessageResolver.resolve("messages.validation.missingParameter", ex.getParameterName());
        return ResponseHandler.exceptionBuilder(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseMessageResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + MessageResolver.resolve(violation.getMessage()))
                .collect(Collectors.joining("; "));
        return ResponseHandler.exceptionBuilder(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<BaseMessageResponse> handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        String errors = Stream.concat(
                        ex.getParameterValidationResults().stream()
                                .flatMap(result -> result.getResolvableErrors().stream()),
                        ex.getCrossParameterValidationResults().stream()
                )
                .map(error -> MessageResolver.resolve(error.getDefaultMessage()))
                .collect(Collectors.joining("; "));
        if (errors.isBlank()) {
            errors = MessageResolver.resolve("messages.request.invalidArgument", ex.getReason());
        }
        return ResponseHandler.exceptionBuilder(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseMessageResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        final String message = MessageResolver.resolve("messages.request.malformedJson", ex.getMostSpecificCause().getMessage());
        return ResponseHandler.exceptionBuilder(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseMessageResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        final String message = MessageResolver.resolve("messages.request.invalidArgument", ex.getMessage());
        return ResponseHandler.exceptionBuilder(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<BaseMessageResponse> handleRateLimitExceededException(RateLimitExceededException ex) {
        RateLimitService.RateLimitDecision decision = ex.getDecision();
        HttpHeaders headers = new HttpHeaders();
        headers.add(RateLimitInterceptor.HEADER_LIMIT, String.valueOf(decision.limit()));
        headers.add(RateLimitInterceptor.HEADER_REMAINING, String.valueOf(decision.remaining()));
        headers.add(RateLimitInterceptor.HEADER_RESET, String.valueOf(decision.resetAfterSeconds()));
        headers.add(RateLimitInterceptor.HEADER_POLICY, decision.policyName());
        headers.add(HttpHeaders.RETRY_AFTER, String.valueOf(decision.retryAfterSeconds()));

        ExceptionMessageResponse response = new ExceptionMessageResponse(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                MessageResolver.resolve("messages.rateLimit.exceeded", decision.retryAfterSeconds())
        );
        return new ResponseEntity<>(response, headers, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseMessageResponse> handleUncheckedException(RuntimeException ex) {
        LOGGER.error("Unhandled unchecked exception", ex);
        return ResponseHandler.exceptionBuilder("messages.request.unexpected", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + MessageResolver.resolve(error.getDefaultMessage());
    }
}
