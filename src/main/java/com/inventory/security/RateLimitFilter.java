package com.inventory.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {
    
    private final ConcurrentHashMap<String, RateLimitInfo> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final long RESET_TIME_MINUTES = 1;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String clientIp = getClientIp(request);
        
        // Skip rate limiting for health check and static endpoints
        String path = request.getRequestURI();
        if (path.equals("/health") || path.startsWith("/api-docs") || path.startsWith("/swagger")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        RateLimitInfo info = requestCounts.computeIfAbsent(clientIp, k -> new RateLimitInfo());
        
        if (info.getCount() >= MAX_REQUESTS_PER_MINUTE) {
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Too many requests. Please try again later.\"}");
            return;
        }
        
        info.increment();
        filterChain.doFilter(request, response);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private static class RateLimitInfo {
        private int count = 0;
        private long resetTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(RESET_TIME_MINUTES);
        
        public synchronized int getCount() {
            if (System.currentTimeMillis() > resetTime) {
                count = 0;
                resetTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(RESET_TIME_MINUTES);
            }
            return count;
        }
        
        public synchronized void increment() {
            count++;
        }
    }
}