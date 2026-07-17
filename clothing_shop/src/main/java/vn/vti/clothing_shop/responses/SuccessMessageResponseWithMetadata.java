package vn.vti.clothing_shop.responses;

public class SuccessMessageResponseWithMetadata extends SuccessMessageResponse {
	public final Object metadata;

	/** Creates SuccessMessageResponseWithMetadata instance. */
	public SuccessMessageResponseWithMetadata(Integer statusCode, Object data, Object metadata) {
		super(statusCode, data);
		this.metadata = metadata;
	}
}
