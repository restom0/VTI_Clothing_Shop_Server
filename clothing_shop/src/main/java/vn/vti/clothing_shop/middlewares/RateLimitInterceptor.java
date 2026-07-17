package vn.vti.clothing_shop.middlewares;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import vn.vti.clothing_shop.exceptions.RateLimitExceededException;
import vn.vti.clothing_shop.services.RateLimitService;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
	public static final String HEADER_LIMIT = "X-RateLimit-Limit";
	public static final String HEADER_REMAINING = "X-RateLimit-Remaining";
	public static final String HEADER_RESET = "X-RateLimit-Reset";
	public static final String HEADER_POLICY = "X-RateLimit-Policy";

	private final RateLimitService rateLimitService;

	/** Runs handle. */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		RateLimitService.RateLimitDecision decision = rateLimitService.consume(request);
		if (!decision.applicable()) {
			return true;
		}

		applyHeaders(response, decision);
		if (!decision.allowed()) {
			throw new RateLimitExceededException(decision);
		}
		return true;
	}

	/** Applies headers. */
	public static void applyHeaders(HttpServletResponse response, RateLimitService.RateLimitDecision decision) {
		response.setHeader(HEADER_LIMIT, String.valueOf(decision.limit()));
		response.setHeader(HEADER_REMAINING, String.valueOf(decision.remaining()));
		response.setHeader(HEADER_RESET, String.valueOf(decision.resetAfterSeconds()));
		response.setHeader(HEADER_POLICY, decision.policyName());
		if (!decision.allowed()) {
			response.setHeader("Retry-After", String.valueOf(decision.retryAfterSeconds()));
		}
	}
}
