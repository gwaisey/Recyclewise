package com.recyclewise.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Strict bucket for auth endpoints (login, register)
    private final Bandwidth authLimit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
    
    // Standard bucket for other public endpoints (e.g. EcoBot)
    private final Bandwidth standardLimit = Bandwidth.classic(30, Refill.intervally(30, Duration.ofMinutes(1)));

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        
        String key = ip + ":" + (path.startsWith("/login") || path.startsWith("/register") ? "auth" : "standard");
        Bucket bucket = buckets.computeIfAbsent(key, k -> {
            if (path.startsWith("/login") || path.startsWith("/register")) {
                return Bucket.builder().addLimit(authLimit).build();
            }
            return Bucket.builder().addLimit(standardLimit).build();
        });

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please try again later.");
            return false;
        }
    }
}
