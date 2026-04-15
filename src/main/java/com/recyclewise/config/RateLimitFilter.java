package com.recyclewise.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1)
public class RateLimitFilter implements Filter {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long WINDOW_MS = 60_000;
    private static final int MAX_API_REQUESTS = 30;
    private static final long API_WINDOW_MS = 60_000;

    private final Map<String, RateLimitEntry> loginAttempts = new ConcurrentHashMap<>();
    private final Map<String, RateLimitEntry> apiRequests = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        String clientIp = getClientIP(httpRequest);

        if (path.contains("/login") || path.contains("/admin/login")) {
            if (!checkRateLimit(clientIp, loginAttempts, MAX_LOGIN_ATTEMPTS, WINDOW_MS)) {
                httpResponse.setStatus(429);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\":\"Too many login attempts. Please try again later.\"}");
                return;
            }
        }

        if (path.startsWith("/api/")) {
            if (!checkRateLimit(clientIp, apiRequests, MAX_API_REQUESTS, API_WINDOW_MS)) {
                httpResponse.setStatus(429);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\":\"Rate limit exceeded. Please slow down.\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean checkRateLimit(String key, Map<String, RateLimitEntry> store,
                                    int maxRequests, long windowMs) {
        long now = Instant.now().toEpochMilli();
        RateLimitEntry entry = store.compute(key, (k, v) -> {
            if (v == null || now - v.windowStart > windowMs) {
                return new RateLimitEntry(now);
            }
            v.count.incrementAndGet();
            return v;
        });
        return entry.count.get() <= maxRequests;
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RateLimitEntry {
        final long windowStart;
        final AtomicInteger count;

        RateLimitEntry(long windowStart) {
            this.windowStart = windowStart;
            this.count = new AtomicInteger(1);
        }
    }
}
