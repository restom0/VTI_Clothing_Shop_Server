package vn.vti.clothing_shop.modules.grpc;

import io.grpc.stub.StreamObserver;
import vn.vti.clothing_shop.exceptions.WrapperException;

public final class GrpcResponseSupport {
	/** Creates GrpcResponseSupport instance. */
	private GrpcResponseSupport() {
	}

	/** Returns value. */
	public static String message(Throwable throwable) {
		if (throwable instanceof WrapperException wrapperException) {
			return wrapperException.message;
		}
		return throwable.getMessage() == null ? "messages.request.unexpected" : throwable.getMessage();
	}

	/** Handles complete. */
	public static <T> void complete(StreamObserver<T> responseObserver, T response) {
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
