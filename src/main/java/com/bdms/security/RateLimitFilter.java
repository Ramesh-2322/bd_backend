package com.bdms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Value("${security.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;

    @Value("${security.rate-limit.login-rpm:20}")
    private int loginRequestsPerMinute;

    @Value("${security.rate-limit.request-create-rpm:40}")
    private int createRequestPerMinute;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();
        int limit = resolveLimit(path, method);
        if (limit <= 0) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = request.getRemoteAddr() + ":" + path + ":" + method;
        Bucket bucket = buckets.computeIfAbsent(clientKey, k -> createBucket(limit));

        if (!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Too many requests\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private int resolveLimit(String path, String method) {
        if ("POST".equalsIgnoreCase(method) && "/api/auth/login".equals(path)) {
            return loginRequestsPerMinute;
        }
        if ("POST".equalsIgnoreCase(method) && "/api/requests".equals(path)) {
            return createRequestPerMinute;
        }
        return -1;
    }

    private Bucket createBucket(int limit) {
        Bandwidth bandwidth = Bandwidth.classic(limit, Refill.greedy(limit, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(bandwidth).build();
    }
}
