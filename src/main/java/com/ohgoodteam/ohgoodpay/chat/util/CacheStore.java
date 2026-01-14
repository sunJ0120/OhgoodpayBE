package com.ohgoodteam.ohgoodpay.recommend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheStore {
    private final RedisTemplate<String, Object> redis;
    private final ObjectMapper objectMapper;

    // 일반 Object 조회
    public <T> T get(String key, Class<T> type) {
        try {
            Object raw = redis.opsForValue().get(key);

            if (raw == null) {
                return null;
            }

            // JSON 문자열로 저장되어 있을 경우
            if (raw instanceof String) {
                return objectMapper.readValue((String) raw, type);
            }

            // 객체로 저장된 경우
            return objectMapper.convertValue(raw, type);
        } catch (Exception e) {
            log.error("Redis GET 실패: {}", e.getMessage());
            return null;
        }
    }

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
