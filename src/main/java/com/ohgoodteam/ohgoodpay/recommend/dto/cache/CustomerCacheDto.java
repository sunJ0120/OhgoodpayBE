package com.ohgoodteam.ohgoodpay.recommend.dto.cache;

import lombok.Builder;
import lombok.Getter;

/**
 * 고객 캐시 정보 DTO
 *
 * Redis에 저장되는 고객 기본 정보를 담는 데이터 전송 객체
 * TTL : 12h
 */
@Getter
@Builder
public class CustomerCacheDto {
    private Long customerId;
    private String name;
    private int creditLimit; // 신용 한도
}