package vn.vti.clothing_shop.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import vn.vti.clothing_shop.constants.Error;
import vn.vti.clothing_shop.constants.Exceptions;
import vn.vti.clothing_shop.exceptions.*;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ResponseHandler {
    public static ResponseEntity<Object> responseBuilder(Integer statusCode, String message, Object object, HttpStatus httpStatus){
        Map<String,Object> response =new HashMap<>();
        response.put("statusCode",statusCode);
        response.put("message",message);
        response.put("object",object);
        return new ResponseEntity<>(response,httpStatus);
    }
    public static ResponseEntity<Object> exceptionBuilder(Exception e){
        Map<String,Object> response = new HashMap<>();
        for (Error exception : Exceptions.exceptions) {
            if (exception.getExceptionClass().isInstance(e)) {
                response.put("statusCode", exception.getStatus().value());
                response.put("message", e.getMessage());
                return new ResponseEntity<>(response, exception.getStatus());
            }
        }
//        if (e instanceof BadRequestException){
//            response.put("statusCode",400);
//            response.put("message",e.getMessage());
//            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
//        }
//        else if (e instanceof UnauthorizeException){
//            response.put("statusCode",401);
//            response.put("message",e.getMessage());
//            return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
//        }
//        else if (e instanceof ForbiddenException){
//            response.put("statusCode",403);
//            response.put("message",e.getMessage());
//            return new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
//        }
//        else if (e instanceof NotFoundException){
//            response.put("statusCode",404);
//            response.put("message",e.getMessage());
//            return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
//        }
//        else if (e instanceof ConflictException){
//            response.put("statusCode",409);
//            response.put("message",e.getMessage());
//            return new ResponseEntity<>(response,HttpStatus.CONFLICT);
//        }
//        else if(e instanceof MissingServletRequestParameterException){
//            response.put("statusCode",400);
//            response.put("message",e.getMessage());
//            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
//        }
//        else if(e instanceof InternalServerErrorException){
//            response.put("statusCode",500);
//            response.put("message",e.getMessage());
//            return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//        }
        response.put("statusCode",500);
        response.put("message",e.getMessage());
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


