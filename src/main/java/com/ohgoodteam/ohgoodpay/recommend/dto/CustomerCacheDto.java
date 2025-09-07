package com.ohgoodteam.ohgoodpay.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/*
레디스 전용 캐싱용 dto

사용자 초기 기초 정보를 저장한다.
 */

@Getter
@ToString
@Builder
@AllArgsConstructor
public class CustomerCacheDto {
    private final Long customerId;
    private final String name;
    private final int limit;
}
