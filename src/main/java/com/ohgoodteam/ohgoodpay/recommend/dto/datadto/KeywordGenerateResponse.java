package com.ohgoodteam.ohgoodpay.recommend.dto.datadto;

import lombok.*;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordGenerateResponse {
    private String keyword;
    private String priceRange; //"10000-40000"
}
