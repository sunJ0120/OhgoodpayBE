package com.ohgoodteam.ohgoodpay.recommend.dto.datadto;

import lombok.*;

/*
네이버 api 요청을 위한 정보를 fast api에 보내는 dto

변경 가능성 있음!!!
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
}
