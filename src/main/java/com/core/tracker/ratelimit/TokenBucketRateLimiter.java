package com.core.tracker.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketRateLimiter implements RateLimiter {

    private final long capacity;
    private final long refillTokensPerSecond;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(long capacity, long refillTokensPerSecond) {
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
    }

    @Override
    public boolean isAllowed(String key) {
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(capacity));
        return bucket.tryConsume(capacity, refillTokensPerSecond);
    }

    private static class Bucket {
        private final AtomicLong tokens;
        private final AtomicLong lastRefillTimestamp;

        public Bucket(long capacity) {
            this.tokens = new AtomicLong(capacity);
            this.lastRefillTimestamp = new AtomicLong(System.currentTimeMillis());
        }

        public boolean tryConsume(long maxCapacity, long refillRate) {
            refill(maxCapacity, refillRate);
            while (true) {
                long currentTokens = tokens.get();
                if (currentTokens <= 0) return false;
                if (tokens.compareAndSet(currentTokens, currentTokens - 1)) return true;
            }
        }

        private void refill(long maxCapacity, long refillRate) {
            long now = System.currentTimeMillis();
            long lastRefill = lastRefillTimestamp.get();
            long elapsedSeconds = (now - lastRefill) / 1000;

            if (elapsedSeconds > 0) {
                if (lastRefillTimestamp.compareAndSet(lastRefill, now)) {
                    long tokensToAdd = elapsedSeconds * refillRate;
                    tokens.updateAndGet(current -> Math.min(maxCapacity, current + tokensToAdd));
                }
            }
        }
    }
}