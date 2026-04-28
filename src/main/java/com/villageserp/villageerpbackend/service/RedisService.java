package com.villageserp.villageerpbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "rt:";

    public void saveRefreshToken(
            String username,
            String refreshToken,
            long expirationSeconds) {

        String key = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                expirationSeconds,
                TimeUnit.SECONDS
        );
    }

    public String getRefreshToken(String username) {
        String key = REFRESH_TOKEN_PREFIX + username;
        return redisTemplate.opsForValue().get(key);
    }

    public boolean validateRefreshToken(
            String username,
            String refreshToken) {

        String storedToken = getRefreshToken(username);
        return storedToken != null && storedToken.equals(refreshToken);
    }

    public void deleteRefreshToken(String username) {
        String key = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.delete(key);
    }
}
