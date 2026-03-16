package lk.helphub.api.infrastructure.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private Cache<String, Bucket> buckets;

    @Value("${app.rate-limit.otp-send.requests-per-minute:5}")
    private int otpSendRequestsPerMinute;

    @Value("${app.rate-limit.otp-verify.requests-per-minute:10}")
    private int otpVerifyRequestsPerMinute;

    @Value("${app.rate-limit.trust-x-forwarded-for:false}")
    private boolean trustXForwardedFor;

    @Value("${app.rate-limit.cache-expiry-minutes:10}")
    private int cacheExpiryMinutes;

    @Value("${app.rate-limit.max-cache-size:10000}")
    private int maxCacheSize;

    @PostConstruct
    public void init() {
        this.buckets = Caffeine.newBuilder()
                .expireAfterAccess(cacheExpiryMinutes, TimeUnit.MINUTES)
                .maximumSize(maxCacheSize)
                .build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIp(request);
        String path = request.getRequestURI();

        Bucket bucket = resolveBucket(clientIp, path);

        if (bucket.tryConsume(1)) {
            return true;
        }

        log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, path);
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
        return false;
    }

    private Bucket resolveBucket(String clientIp, String path) {
        String bucketKey = clientIp;

        // Different rate limits based on endpoint
        if (path.contains("/verification/send")) {
            bucketKey = clientIp + ":send";
        } else if (path.contains("/verification/verify")) {
            bucketKey = clientIp + ":verify";
        }

        return buckets.get(bucketKey, k -> createBucket(path));
    }

    private Bucket createBucket(String path) {
        int requestsPerMinute;
        if (path.contains("/verification/send")) {
            requestsPerMinute = otpSendRequestsPerMinute;
        } else if (path.contains("/verification/verify")) {
            requestsPerMinute = otpVerifyRequestsPerMinute;
        } else {
            requestsPerMinute = otpSendRequestsPerMinute; // default
        }

        return Bucket.builder()
                .addLimit(Bandwidth.classic(requestsPerMinute, Refill.greedy(requestsPerMinute, Duration.ofMinutes(1))))
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        if (trustXForwardedFor) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }
}
