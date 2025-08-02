package vn.vti.clothing_shop.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseCheckedException {
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
