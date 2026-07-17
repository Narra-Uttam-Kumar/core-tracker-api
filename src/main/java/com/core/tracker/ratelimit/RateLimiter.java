package com.core.tracker.ratelimit;

public interface RateLimiter {
    boolean isAllowed(String key);
}