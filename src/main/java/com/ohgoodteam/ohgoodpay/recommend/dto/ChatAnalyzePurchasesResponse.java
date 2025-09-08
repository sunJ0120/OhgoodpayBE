package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class ChatAnalyzePurchasesResponse extends BaseChatResponse {
    private String analyzedCategory;
}
