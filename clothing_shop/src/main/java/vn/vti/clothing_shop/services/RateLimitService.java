package vn.vti.clothing_shop.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import vn.vti.clothing_shop.configs.RateLimitProperties;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final RateLimitDecision SKIPPED = new RateLimitDecision(
            false,
            true,
            "disabled",
            0,
            0,
            0,
            0
    );

    private final RateLimitProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final AtomicLong lastCleanupNanos = new AtomicLong();

    public RateLimitDecision consume(HttpServletRequest request) {
        if (!properties.isEnabled() || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return SKIPPED;
        }

        String path = normalizedPath(request);
        if (isExcluded(path)) {
            return SKIPPED;
        }

        String method = request.getMethod().toUpperCase(Locale.ROOT);
        RateLimitProperties.Policy policy = findPolicy(path, method);
        if (policy.getCapacity() <= 0 || policy.getRefillTokens() <= 0 || policy.getRefillPeriod().isZero() || policy.getRefillPeriod().isNegative()) {
            return SKIPPED;
        }

        long now = System.nanoTime();
        String key = policy.getName() + ":" + clientIdentity(request);
        RateLimitDecision decision = buckets.computeIfAbsent(key, ignored -> new TokenBucket(now))
                .consume(policy, now);
        cleanup(now);
        return decision;
    }

    private RateLimitProperties.Policy findPolicy(String path, String method) {
        for (RateLimitProperties.Policy policy : properties.getRoutes()) {
            if (!policy.matchesMethod(method)) {
                continue;
            }
            for (String pattern : policy.getPathPatterns()) {
                if (pathMatcher.match(pattern, path)) {
                    return policy;
                }
            }
        }
        return properties.getDefaultPolicy();
    }

    private boolean isExcluded(String path) {
        for (String excludedPath : properties.getExcludedPaths()) {
            if (pathMatcher.match(excludedPath, path)) {
                return true;
            }
        }
        return false;
    }

    private String clientIdentity(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && !authorization.isBlank()) {
            return "auth:" + hash(authorization);
        }
        return "ip:" + clientIp(request);
    }

    private String clientIp(HttpServletRequest request) {
        if (properties.isTrustProxyHeaders()) {
            String forwardedValue = request.getHeader(properties.getClientIpHeader());
            if (forwardedValue != null && !forwardedValue.isBlank()) {
                return forwardedValue.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed, 0, 8);
        } catch (NoSuchAlgorithmException exception) {
            return Integer.toHexString(value.hashCode());
        }
    }

    private String normalizedPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String path = request.getRequestURI();
        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        if (path.startsWith("/api/")) {
            path = path.substring("/api".length());
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.isBlank() ? "/" : path.toLowerCase(Locale.ROOT);
    }

    private void cleanup(long now) {
        long cleanupInterval = toNanos(properties.getCleanupInterval(), Duration.ofMinutes(5));
        long previous = lastCleanupNanos.get();
        if (now - previous < cleanupInterval || !lastCleanupNanos.compareAndSet(previous, now)) {
            return;
        }

        long bucketTtl = toNanos(properties.getBucketTtl(), Duration.ofMinutes(10));
        buckets.entrySet().removeIf(entry -> entry.getValue().isIdle(now, bucketTtl));
    }

    private long toNanos(Duration duration, Duration fallback) {
        Duration resolved = duration == null || duration.isZero() || duration.isNegative() ? fallback : duration;
        return resolved.toNanos();
    }

    public record RateLimitDecision(
            boolean applicable,
            boolean allowed,
            String policyName,
            int limit,
            int remaining,
            long retryAfterSeconds,
            long resetAfterSeconds
    ) {
    }

    private static final class TokenBucket {
        private double tokens;
        private long lastRefillNanos;
        private long lastAccessNanos;
        private boolean initialized;

        private TokenBucket(long now) {
            this.lastRefillNanos = now;
            this.lastAccessNanos = now;
        }

        private synchronized RateLimitDecision consume(RateLimitProperties.Policy policy, long now) {
            refill(policy, now);
            boolean allowed = tokens >= 1D;
            if (allowed) {
                tokens -= 1D;
            }

            lastAccessNanos = now;
            int remaining = Math.max(0, (int) Math.floor(tokens));
            long retryAfterSeconds = allowed ? 0 : secondsUntilTokens(policy, 1D - tokens);
            long resetAfterSeconds = secondsUntilTokens(policy, policy.getCapacity() - tokens);
            return new RateLimitDecision(
                    true,
                    allowed,
                    policy.getName(),
                    policy.getCapacity(),
                    remaining,
                    retryAfterSeconds,
                    resetAfterSeconds
            );
        }

        private void refill(RateLimitProperties.Policy policy, long now) {
            if (!initialized) {
                tokens = policy.getCapacity();
                lastRefillNanos = now;
                initialized = true;
                return;
            }

            long elapsedNanos = now - lastRefillNanos;
            if (elapsedNanos <= 0) {
                return;
            }

            double refill = (double) elapsedNanos / policy.getRefillPeriod().toNanos() * policy.getRefillTokens();
            tokens = Math.min(policy.getCapacity(), tokens + refill);
            lastRefillNanos = now;
        }

        private long secondsUntilTokens(RateLimitProperties.Policy policy, double missingTokens) {
            if (missingTokens <= 0D) {
                return 0;
            }
            double nanos = missingTokens * policy.getRefillPeriod().toNanos() / policy.getRefillTokens();
            return Math.max(1L, (long) Math.ceil(nanos / NANOS_PER_SECOND));
        }

        private boolean isIdle(long now, long ttlNanos) {
            return now - lastAccessNanos > ttlNanos;
        }
    }
}
