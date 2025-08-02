package vn.vti.clothing_shop.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseCheckedException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
