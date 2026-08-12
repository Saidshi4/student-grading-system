package com.supremecourt.studentgradingsystem.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void addBlacklist(String accessToken, Long TTL) {
        String key = DigestUtils.md5DigestAsHex(accessToken.getBytes()) + " Old Access Token:";
        redisTemplate.opsForValue().
                set(key, accessToken, TTL, TimeUnit.MINUTES);
    }

    public boolean isBlacklisted(String oldAccessToken) {
        String hash = DigestUtils.md5DigestAsHex(oldAccessToken.getBytes());
        String value = (String) redisTemplate.opsForValue().get(hash + " Old Access Token:");
        return value != null && value.equals(oldAccessToken);
    }
}