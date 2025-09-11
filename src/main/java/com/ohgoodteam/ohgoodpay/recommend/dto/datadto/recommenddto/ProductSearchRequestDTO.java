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
public class ProductSearchRequestDTO {
    private String keyword;
    private String priceRange;
    private Integer maxResults;

    public static ProductSearchRequestDTO of(String keyword, String priceRange, Integer maxResults) {
        return ProductSearchRequestDTO.builder()
                .keyword(keyword)
                .priceRange(priceRange)
                .maxResults(maxResults)
                .build();
    }
}
