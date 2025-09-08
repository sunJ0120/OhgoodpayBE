package com.ohgoodteam.ohgoodpay.recommend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CacheStore {
    // 정의해둔 REDIS TEMPLATE를 가져옴
    private final RedisTemplate<String, Object> redis;

    // 키를 한 곳에서 만든다.
    private String key(CacheSpec spec, Object userId){
        return spec.getPrefix() + ":user:" + userId;
    }

    // put 첫번째는 그 스펙(CacheSpec)에 정해둔 기본 TTL
    // put 두번째는 정의한 ttl을 사용하고 싶을때 사용한다.
    public void put(CacheSpec spec, Object userId, Object value) {
        put(spec, userId, value, spec.getTtl());
    }

    public void put(CacheSpec spec, Object userId, Object value, Duration ttl) {
        redis.opsForValue().set(key(spec, userId), value, ttl);
    }

    // redis는 Object만 반환하기 때문에 DTO를 꺼내 쓰려면 캐스팅이 필수이다.
    // 그래서 redis.opsForValue().get(...)의 값은 항상 Object이기 때문에 이걸 정의해서 서비스 단에서 캐스팅을 하지 않도록 해야 한다.
    public <T> T get(CacheSpec spec, Object userId, Class<T> type) {
        Object raw = redis.opsForValue().get(key(spec, userId));
        return type.cast(raw); //typecast를 한 번 해주기 때문에, 잘못된 타입일경우 ClassCastException가 발생한다.
    }

    // DB에서 해당 데이터가 바뀌었거나 없어졌을 때, 캐시도 지워서 일관적으로 만들기 위함이다.
    // “TTL 만료”를 기다리지 않고 즉시 무효화할 때 사용한다.
    public void evict(CacheSpec spec, Object userId) {
        redis.delete(key(spec, userId));
    }

    // Read-Through: 캐시 미스 시 로더 실행 → 캐시에 저장 후 반환하도록 설정한다.
    public <T> T getOrLoad(CacheSpec spec, Object userId, Class<T> type, Supplier<T> loader) {
        // 1) 캐시 먼저 조회
        T cached = this.get(spec, userId, type);
        if (cached != null) {
            return cached;
        }

        // 2) 미스면 "로더" 실행 (여기서 DB 접근이 수행됨)
        // 여기서 Supplier<T>를 사용해서 조회 로직을 숨길 수 있다. 이를 통해 조회 로직이 서비스 단으로 새어나가지 않도록 한다.
        T loaded = loader.get();

        // 3) 값이 있으면 캐시에 넣고 반환 (키/TTL은 spec에서 가져옴)
        if (loaded != null) {
            this.put(spec, userId, loaded, spec.getTtl());
        }
        return loaded;
    }
}
