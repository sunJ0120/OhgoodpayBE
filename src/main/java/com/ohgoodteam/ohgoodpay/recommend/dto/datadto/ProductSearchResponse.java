package com.ohgoodteam.ohgoodpay.recommend.dto.datadto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchResponse {
    private List<ProductDto> products;
}
