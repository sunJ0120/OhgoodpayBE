package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.ProductDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatRecommendResponse extends BaseChatResponse {
    private ProductDto item;
    private String message;
    private Boolean hasMore;
    private Integer remainingCount;
    private String nextStep;
}
