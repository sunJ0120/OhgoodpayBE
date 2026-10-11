package com.ohgoodteam.ohgoodpay.chat.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheStore {
    private final RedisTemplate<String, Object> redis;
    private final ObjectMapper objectMapper;

    // 단건 조회. 없거나 읽기에 실패하면 null
    public <T> T get(String key, Class<T> type) {
        try {
            Object raw = redis.opsForValue().get(key);
            return raw == null ? null : objectMapper.convertValue(raw, type);
        } catch (Exception e) {
            log.error("Redis GET 실패: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    // 저장
    public void save(String key, Object value, Duration ttl) {
        redis.opsForValue().set(key, value, ttl);
    }

    // 삭제
    public void delete(String key) {
        redis.delete(key);
    }
}
