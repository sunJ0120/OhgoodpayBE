package com.ohgoodteam.ohgoodpay.recommend.dto.datadto;

import lombok.*;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendMessageRequest {
    private String userId;
    private String username;
    private ProductDto product;
    private ComsumerContextDto comsumerContextDto;
}
