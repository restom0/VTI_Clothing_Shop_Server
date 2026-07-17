package vn.vti.clothing_shop.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseCheckedException {
	/** Creates ForbiddenException instance. */
	public ForbiddenException(String message) {
		super(HttpStatus.FORBIDDEN, message);
	}
}
