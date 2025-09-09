package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto;

import lombok.*;
import java.util.List;

/**
 * FAST API - 네이버 쇼핑 api 결과 저장하는 DTO
 *
 * 네이버 쇼핑 api 결과들을 담는 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchResponse {
    private List<ProductDto> products;
}
