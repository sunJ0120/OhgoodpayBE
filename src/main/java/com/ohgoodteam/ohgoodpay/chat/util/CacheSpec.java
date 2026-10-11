package com.ohgoodteam.ohgoodpay.chat.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public enum CacheSpec {
    FLOW("session:flow", Duration.ofHours(1)),
    COUNT("session:count", Duration.ofHours(1)),
    MOOD("session:mood", Duration.ofHours(1)),
    SUMMARY("session:summary", Duration.ofHours(1)),
    PREV_KEYWORD("session:prev-keyword", Duration.ofHours(1));

    private final String prefix; //키 네임 스페이스
    private final Duration ttl;

    public String key(String sessionId) {
        return this.prefix + ":" + sessionId;
    }
}
