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
 * 6) 플로우 (string) - flow
 * 7) 카운트 (string) - count
 * 8) 추천 상품 (productDTO) - products
 */
@Getter
@RequiredArgsConstructor
public enum CacheSpec {
    // 1) 사용자 스냅샷(JSON) - 12시간, 유저 기반
    CUSTOMER("v1:customer", Duration.ofHours(12)),
    // 2) 잔액(String) - 3분, 유저 기반
    BALANCE("v1:balance", Duration.ofMinutes(3)),
    // 3) 취미(List<String> or Set<String>) - 5분, 유저 기반
    HOBBY("v1:hobby", Duration.ofMinutes(5)),
    // 4) 기분 데이터(String) - 10분, 세션 기반
    MOOD("v1:mood", Duration.ofMinutes(10)),
    // 5) 대화 요약본 - 1시간, 세션 기반
    SUMMARY("v1:summary", Duration.ofHours(1)), //메세지들 캐싱을 위해서 선언
    // 6) 플로우 - 1시간, 세션 기반
    FLOW("v1:flow", Duration.ofHours(1)), //플로우 캐싱을 위해 선언
    // 7) 카운트 - 1시간, 세션 기반
    COUNT("v1:count", Duration.ofHours(1)), //플로우 카운트를 위해 선언
    // 8) 추천 상품 - 1시간, 세션 기반
    PRODUCTS("v1:products", Duration.ofHours(1)); //추천 상품을 저장하기 위해 선언

    private final String prefix; //키 네임 스페이스
    private final Duration ttl;
}
