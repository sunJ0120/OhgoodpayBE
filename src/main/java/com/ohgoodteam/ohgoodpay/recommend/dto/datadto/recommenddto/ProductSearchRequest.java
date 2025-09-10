package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto;

import lombok.*;

/**
 * FAST API - product에 들어가는 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchRequest {
    private String keyword;
    private String priceRange;
    private Integer maxResults;

    public static ProductSearchRequest of(String keyword, String priceRange, Integer maxResults) {
        return ProductSearchRequest.builder()
                .keyword(keyword)
                .priceRange(priceRange)
                .maxResults(maxResults)
                .build();
    }
}
