package vn.vti.clothing_shop.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import vn.vti.clothing_shop.exceptions.WrapperException;

public class ResponseHandler {
	/** Creates ResponseHandler instance. */
	private ResponseHandler() {
		throw new IllegalStateException("Utility class");
	}

	/** Builds builder. */
	public static ResponseEntity<BaseMessageResponse> successBuilder(HttpStatus status, Object data) {
		SuccessMessageResponse response = new SuccessMessageResponse(status.value(), MessageResolver.resolveIfMessageKey(data));
		return new ResponseEntity<>(response, status);
	}

	/** Builds builder. */
	public static ResponseEntity<BaseMessageResponse> successBuilder(HttpStatus status, Object data, Object metadata) {
		SuccessMessageResponseWithMetadata response = new SuccessMessageResponseWithMetadata(status.value(),
		                                                                                     MessageResolver.resolveIfMessageKey(
				                                                                                     data), metadata);
		return new ResponseEntity<>(response, status);
	}

	/** Builds builder. */
	public static ResponseEntity<BaseMessageResponse> exceptionBuilder(WrapperException ex) {
		ExceptionMessageResponse response = new ExceptionMessageResponse(ex.statusCode.value(),
		                                                                 MessageResolver.resolve(ex.message));
		return new ResponseEntity<>(response, ex.statusCode);
	}

	/** Builds builder. */
	public static ResponseEntity<BaseMessageResponse> exceptionBuilder(String message, HttpStatus status) {
		ExceptionMessageResponse response = new ExceptionMessageResponse(status.value(), MessageResolver.resolve(message));
		return new ResponseEntity<>(response, status);
	}
}


