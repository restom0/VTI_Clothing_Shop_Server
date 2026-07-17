package vn.vti.clothing_shop.exceptions;

import lombok.Getter;
import vn.vti.clothing_shop.services.RateLimitService;

@Getter
public class RateLimitExceededException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final transient RateLimitService.RateLimitDecision decision;

	/** Creates RateLimitExceededException instance. */
	public RateLimitExceededException(RateLimitService.RateLimitDecision decision) {
		super("messages.rateLimit.exceeded");
		this.decision = decision;
	}
}
