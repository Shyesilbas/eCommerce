package com.serhat.ecommerce.config.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final RedisTemplate<String,String> redisTemplate;

    public boolean isAllowed(Long userId, int maxRequests, int timeWindowInSeconds) {
        String key = "rate_limit:user:" + userId;
        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount == 1) {
            redisTemplate.expire(key, timeWindowInSeconds, TimeUnit.SECONDS);
        }

        return currentCount <= maxRequests;
    }
}
