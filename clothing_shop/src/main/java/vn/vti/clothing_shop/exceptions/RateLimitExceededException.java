package vn.vti.clothing_shop.exceptions;

import lombok.Getter;
import vn.vti.clothing_shop.services.RateLimitService;

@Getter
public class RateLimitExceededException extends RuntimeException {
	private final RateLimitService.RateLimitDecision decision;

	public RateLimitExceededException(RateLimitService.RateLimitDecision decision) {
		super("messages.rateLimit.exceeded");
		this.decision = decision;
	}
}
