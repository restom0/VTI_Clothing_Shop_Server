package vn.vti.clothing_shop.responses;

public class BaseMessageResponse {
    private final Integer statusCode;

    public BaseMessageResponse(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
