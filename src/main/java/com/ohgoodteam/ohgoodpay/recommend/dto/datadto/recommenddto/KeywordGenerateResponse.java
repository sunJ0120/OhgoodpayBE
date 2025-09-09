package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto;

import lombok.*;

/**
 * FAST API - LLM 응답 검색 키워드 DTO
 *
 * LLM이 주는 추천 키워드를 담는 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordGenerateResponse {
    private String keyword;
    private String priceRange; //"10000-40000"
}
