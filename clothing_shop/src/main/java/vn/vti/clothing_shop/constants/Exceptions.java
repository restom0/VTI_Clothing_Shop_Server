package vn.vti.clothing_shop.constants;

import org.springframework.http.HttpStatus;
import vn.vti.clothing_shop.exceptions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Exceptions {
    public static final ArrayList<Error> exceptions = new ArrayList<Error>(
            List.of(
                    new Error(NotFoundException.class, HttpStatus.NOT_FOUND),
                    new Error(ConflictException.class, HttpStatus.CONFLICT),
                    new Error(BadRequestException.class, HttpStatus.BAD_REQUEST),
                    new Error(UnauthorizeException.class, HttpStatus.UNAUTHORIZED),
                    new Error(ForbiddenException.class, HttpStatus.FORBIDDEN),
                    new Error(InternalServerErrorException.class, HttpStatus.INTERNAL_SERVER_ERROR),
                    new Error(MissingServletRequestParameterException.class, HttpStatus.BAD_REQUEST),
                    new Error(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST),
                    new Error(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST)
            )
    );
}
