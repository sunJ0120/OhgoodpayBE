package com.ohgoodteam.ohgoodpay.recommend.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

/**
 *  REDIS -> 전체 메세지 시간은 1H
 *  상품 같은 경우는 -> 디폴트 상품을 REDIS에 넣어둬도 될 것 같다.
 */

/**
 * REDIS 캐싱을 위한 TTL 설정 값
 *
 * 캐시에 들어가야 할 값은 다음과 같다.
 * 1) 사용자 스냅샷(JSON) - {name, id, creditLimit}
 * 2) 잔액(int) - balance
 * 3) 취미(string) - hobby
 * [보류] 4) 최근 구매한 제품 카테고리(string) - recent_purchase
 * 5) 기분 (string) - mood
 */
@Getter
@RequiredArgsConstructor
public enum CacheSpec {
    // 1) 사용자 스냅샷(JSON)
    CUSTOMER("v1:customer", Duration.ofHours(12)),
    // 2) 잔액(String) - 3분
    BALANCE("v1:balance", Duration.ofMinutes(3)),
    // 3) 취미(List<String> or Set<String>) - 5분
    HOBBY("v1:hobby", Duration.ofMinutes(5)),
//    // 4) 최근 구매한 제품 카테고리(List<String> or Set<String>) - 5분
//    RECENT_PURCHASE("v1:recent_purchase", Duration.ofMinutes(5)),
    // 5) 기분 데이터(String) - 10분
    MOOD("v1:mood", Duration.ofMinutes(10)),
//    // 6) TOPN 추천 제품 캐싱 (List<RecommendProductDto>) - 10분
//    RECOMMEND_PRODUCTS("v1:recommend_products", Duration.ofMinutes(10));

    SUMMARY("v1:summary", Duration.ofHours(12)); //메세지들 캐싱을 위해서 선언

    private final String prefix; //키 네임 스페이스
    private final Duration ttl;
}
