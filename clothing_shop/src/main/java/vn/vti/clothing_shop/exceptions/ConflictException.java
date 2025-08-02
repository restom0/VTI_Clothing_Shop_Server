package vn.vti.clothing_shop.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseCheckedException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
