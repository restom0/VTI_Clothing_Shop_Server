package vn.vti.clothing_shop.responses;

public class SuccessMessageResponse extends BaseMessageResponse {
	public final Object data;

	/** Creates SuccessMessageResponse instance. */
	public SuccessMessageResponse(Integer statusCode, Object data) {
		super(statusCode);
		this.data = data;
	}
}
