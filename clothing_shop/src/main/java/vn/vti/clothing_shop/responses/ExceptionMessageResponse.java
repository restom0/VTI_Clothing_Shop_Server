package vn.vti.clothing_shop.responses;

public class ExceptionMessageResponse extends BaseMessageResponse {
    public final String message;

    public ExceptionMessageResponse(Integer statusCode, String message) {
        super(statusCode);
        this.message = message;
    }
}
