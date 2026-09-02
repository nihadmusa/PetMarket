package com.example.animalservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ViewCountService {

    private static final String KEY_PREFIX = "animal:view:";

    private final StringRedisTemplate redisTemplate;

    public Long incrementView(UUID animalId) {
        return redisTemplate.opsForValue().increment(KEY_PREFIX + animalId);
    }

    public Long getViews(UUID animalId) {
        var value = redisTemplate.opsForValue().get(KEY_PREFIX + animalId);
        return value == null ? 0L : Long.parseLong(value);
    }
}
