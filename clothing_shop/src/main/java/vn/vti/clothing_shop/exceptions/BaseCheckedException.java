package vn.vti.clothing_shop.exceptions;

import org.springframework.http.HttpStatus;

public class BaseCheckedException extends Exception {
    public final HttpStatus statusCode;
    public final String message;

    public BaseCheckedException(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.message = message;
    }

}
