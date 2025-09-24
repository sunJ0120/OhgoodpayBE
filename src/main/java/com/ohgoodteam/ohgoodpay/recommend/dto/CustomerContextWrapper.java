package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.common.dto.CustomerCacheDTO;
import lombok.Builder;
import lombok.Getter;

/**
 * 캐싱하는 정보를 전부 하나로 묶어서 service 로직에서 수집하기 위한 wrapper dto
 */
@Builder
@Getter
public class CustomerContextWrapper {
    private final CustomerCacheDTO customerInfo;
    private final String hobby;
    private final String mood;
    private final Integer balance;
    private final String summary;
}