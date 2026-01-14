package com.ohgoodteam.ohgoodpay.chat.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public enum CacheSpec {
    HISTORY("session:history", Duration.ofHours(1)),
    PRODUCTS("session:products", Duration.ofHours(1));

    private final String prefix; //키 네임 스페이스
    private final Duration ttl;

    public String key(String sessionId) {
        return this.prefix + ":" + sessionId;
    }
}
