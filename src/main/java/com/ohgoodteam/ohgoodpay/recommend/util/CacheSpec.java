package com.ohgoodteam.ohgoodpay.recommend.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

/**
 * REDIS 캐싱을 위한 TTL 설정 값
 * 세션별 관리는 앱세션 (화면 나갔다 들어오면 초기화)을 기준으로 한다.
 *
 * 캐시에 들어가야 할 값은 다음과 같다.
 * 1) 사용자 스냅샷(JSON) - {name, id, creditLimit}
 * 2) 잔액(int) - balance
 * 3) 취미(string) - hobby
 * 4) 기분 (string) - mood
 * 5) 대화 요약본 (string) - summary
 */
@Getter
@RequiredArgsConstructor
public enum CacheSpec {
    // 1) 사용자 스냅샷(JSON), 12시간
    CUSTOMER("v1:customer", Duration.ofHours(12)),
    // 2) 잔액(String) - 3분
    BALANCE("v1:balance", Duration.ofMinutes(3)),
    // 3) 취미(List<String> or Set<String>) - 5분
    HOBBY("v1:hobby", Duration.ofMinutes(5)),
    // 4) 기분 데이터(String) - 10분, 세션 초기화 시 초기화 하도록 구성
    MOOD("v1:mood", Duration.ofMinutes(10)),
    // 5) 대화 요약본 - 1시간, 세션 초기화 시 초기화 하도록 구성
    SUMMARY("v1:summary", Duration.ofHours(1)), //메세지들 캐싱을 위해서 선언
    // 6) 플로우 - 1시간, 세션 초기화 시 초기화 하도록 구성
    FLOW("v1:flow", Duration.ofHours(1)), //플로우 캐싱을 위해 선언
    // 7) 카운트 - 1시간, 세션 초기화 시 초기화 하도록 구성
    COUNT("v1:count", Duration.ofHours(1)); //플로우 카운트를 위해 선언

    private final String prefix; //키 네임 스페이스
    private final Duration ttl;
}
