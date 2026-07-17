package vn.vti.clothing_shop.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseCheckedException {
	/** Creates NotFoundException instance. */
	public NotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}
}
