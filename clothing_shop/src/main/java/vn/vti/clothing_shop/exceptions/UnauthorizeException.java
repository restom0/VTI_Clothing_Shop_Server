package vn.vti.clothing_shop.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizeException extends BaseCheckedException {
    public UnauthorizeException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
