package vn.vti.clothing_shop.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WrapperException extends Exception {
    public final HttpStatus statusCode;
    public final String message;

    public WrapperException(BaseCheckedException cause) {
        super(cause);
        statusCode = cause.statusCode;
        message = cause.message;
    }
}
