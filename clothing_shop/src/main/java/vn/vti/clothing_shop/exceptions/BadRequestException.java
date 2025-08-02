package vn.vti.clothing_shop.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseCheckedException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
