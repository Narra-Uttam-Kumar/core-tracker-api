package com.core.tracker.ratelimit;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private final RateLimiter rateLimiter;

    public RateLimitingFilter(
            @Value("${app.rate-limit.capacity}") long capacity,
            @Value("${app.rate-limit.refill-rate}") long refillRate) {
        this.rateLimiter = new TokenBucketRateLimiter(capacity, refillRate);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = httpRequest.getRemoteAddr();

        if (!rateLimiter.isAllowed(clientIp)) {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"message\": \"Too many traffic requests. Core rate limit exceeded.\"}");
            return;
        }
        chain.doFilter(request, response);
    }
}