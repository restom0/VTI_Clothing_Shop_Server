package vn.vti.clothing_shop.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseCheckedException {
	/** Creates ConflictException instance. */
	public ConflictException(String message) {
		super(HttpStatus.CONFLICT, message);
	}
}
