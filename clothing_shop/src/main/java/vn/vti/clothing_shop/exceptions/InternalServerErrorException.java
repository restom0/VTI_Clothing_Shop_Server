package vn.vti.clothing_shop.exceptions;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message){
        super(message);
    }
}
