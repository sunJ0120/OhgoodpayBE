package com.ohgoodteam.ohgoodpay.chat.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheStore {
    private final RedisTemplate<String, Object> redis;
    private final ObjectMapper objectMapper;

    // List 조회 - 히스토리 조회용
    public <T> List<T> getList(String key, Class<T> type) {
        try {
            Object raw = redis.opsForValue().get(key);

            if (raw == null) {
                return new ArrayList<>();
            }

            return objectMapper.convertValue(
                    raw,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, type)
            );
        } catch (Exception e) {
            log.error("Redis GET List 실패: key={}, error={}", key, e.getMessage());
            return new ArrayList<>();
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
