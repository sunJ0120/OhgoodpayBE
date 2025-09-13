package com.ohgoodteam.ohgoodpay.recommend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

/**
 * REDIS 캐시 저장소
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CacheStore {
    private final RedisTemplate<String, Object> redis;

    // StringRedisTemplate 추가 (Fast API 호환용)
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    // 기존 키 생성 메서드들은 그대로...
    private String key(CacheSpec spec, Object userId){
        return spec.getPrefix() + ":user:" + userId;
    }

    private String sessionKey(CacheSpec spec, String sessionId){
        return spec.getPrefix() + ":session:" + sessionId;
    }

    // put 메서드들은 그대로...
    public void put(CacheSpec spec, Object userId, Object value) {
        put(spec, userId, value, spec.getTtl());
    }

    public void put(CacheSpec spec, Object userId, Object value, Duration ttl) {
        redis.opsForValue().set(key(spec, userId), value, ttl);
    }

    public void putBySession(CacheSpec spec, String sessionId, Object value) {
        putBySession(spec, sessionId, value, spec.getTtl());
    }

    public void putBySession(CacheSpec spec, String sessionId, Object value, Duration ttl) {
        redis.opsForValue().set(sessionKey(spec, sessionId), value, ttl);
    }

    // get 메서드 수정 - SerializationException 처리 추가
    public <T> T get(CacheSpec spec, Object userId, Class<T> type) {
        try {
            log.info("Redis GET 시도: {}", key(spec, userId));
            Object raw = redis.opsForValue().get(key(spec, userId));
            log.info("Redis GET 결과: {}", raw);
            return raw != null ? type.cast(raw) : null;
        } catch (ClassCastException | org.springframework.data.redis.serializer.SerializationException e) {
            log.warn("JSON 역직렬화 실패, 문자열로 재시도: {}", e.getMessage());
            try {
                // Fast API가 저장한 순수 JSON 문자열로 재시도
                String stringValue = stringRedisTemplate.opsForValue().get(key(spec, userId));
                if (stringValue != null) {
                    return objectMapper.readValue(stringValue, type);
                }
            } catch (Exception ex) {
                log.error("문자열 역직렬화도 실패: {}", ex.getMessage());
            }
            return null;
        } catch (Exception e) {
            log.error("Redis GET 실패: {}", e.getMessage());
            throw e;
        }
    }

    // sessionId 기반 get 메서드도 동일하게 수정
    public <T> T getBySession(CacheSpec spec, String sessionId, Class<T> type) {
        try {
            log.info("Redis GET 시도 (session): {}", sessionKey(spec, sessionId));
            Object raw = redis.opsForValue().get(sessionKey(spec, sessionId));
            log.info("Redis GET 결과 (session): {}", raw);
            return raw != null ? type.cast(raw) : null;
        } catch (ClassCastException | org.springframework.data.redis.serializer.SerializationException e) {
            log.warn("JSON 역직렬화 실패 (session), 문자열로 재시도: {}", e.getMessage());
            try {
                String stringValue = stringRedisTemplate.opsForValue().get(sessionKey(spec, sessionId));
                if (stringValue != null) {
                    return objectMapper.readValue(stringValue, type);
                }
            } catch (Exception ex) {
                log.error("문자열 역직렬화도 실패 (session): {}", ex.getMessage());
            }
            return null;
        } catch (Exception e) {
            log.error("Redis GET 실패 (session): {}", e.getMessage());
            throw e;
        }
    }

    // 나머지 메서드들은 그대로...
    public void pushList(CacheSpec spec, Object userId, List<?> values) {
        redis.opsForList().rightPushAll(key(spec, userId), values.toArray());
        redis.expire(key(spec, userId), spec.getTtl());
    }

    public <T> T getByIndex(CacheSpec spec, Object userId, int index, Class<T> type) {
        Object raw = redis.opsForList().index(key(spec, userId), index);
        return raw != null ? type.cast(raw) : null;
    }

    public void evict(CacheSpec spec, Object userId) {
        redis.delete(key(spec, userId));
    }

    public <T> T getOrLoad(CacheSpec spec, Object userId, Class<T> type, Supplier<T> loader) {
        T cached = this.get(spec, userId, type);
        if (cached != null) {
            return cached;
        }

        T loaded = loader.get();
        if (loaded != null) {
            this.put(spec, userId, loaded, spec.getTtl());
        }
        return loaded;
    }
}
