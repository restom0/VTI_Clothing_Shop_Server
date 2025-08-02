package vn.vti.clothing_shop.responses;

import org.springframework.http.HttpStatusCode;

public class ErrorMessageResponse extends BaseMessageResponse {
    private final String message;

    public ErrorMessageResponse(HttpStatusCode httpStatusCode, String message) {
        super(httpStatusCode.value());
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
