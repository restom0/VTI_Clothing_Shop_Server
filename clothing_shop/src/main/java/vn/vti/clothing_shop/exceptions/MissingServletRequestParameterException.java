package vn.vti.clothing_shop.exceptions;

public class MissingServletRequestParameterException extends RuntimeException{
    public MissingServletRequestParameterException(String message){
        super(message);
    }
}
