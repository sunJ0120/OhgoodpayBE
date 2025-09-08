package com.ohgoodteam.ohgoodpay.recommend.dto.cache;

import lombok.Builder;
import lombok.Getter;

/**
 * 고객 캐시 정보 DTO
 * 초반에 Redis에 저장될 고객 기본 정보
 */
@Getter
@Builder
public class CustomerCacheDto {
    private Long customerId;
    private String name;
    private int creditLimit; // 신용 한도
}