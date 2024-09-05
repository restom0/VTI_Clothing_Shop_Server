package vn.vti.clothing_shop.exceptions;

public class UnauthorizeException extends RuntimeException {
    public UnauthorizeException(String message){
        super(message);
    }
}
