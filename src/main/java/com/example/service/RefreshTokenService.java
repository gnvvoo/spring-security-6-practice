package com.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String email, String refreshToken) {
        String key = "refresh:" + email;

        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofDays(14));
    }

    public String find(String email) {
        String key = "refresh:" + email;

        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String email) {
        String key = "refresh:" + email;

        redisTemplate.delete(key);
    }
}
