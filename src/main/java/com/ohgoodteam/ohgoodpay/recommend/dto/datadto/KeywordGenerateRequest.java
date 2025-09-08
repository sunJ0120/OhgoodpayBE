package com.ohgoodteam.ohgoodpay.recommend.dto.datadto;

import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDto;
import lombok.*;

/*
사용자 정보와 요청을 담은 dto
구조 변동 가능성 있음.....!
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordGenerateRequest {
    private CustomerCacheDto customerInfo;
    private String mood;
    private String hobby;
    private String category;
    private Integer balance;
}
