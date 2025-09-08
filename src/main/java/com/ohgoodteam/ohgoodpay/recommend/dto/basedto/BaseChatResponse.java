package com.ohgoodteam.ohgoodpay.recommend.dto.basedto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
public abstract class BaseChatResponse {
    private String message;
    private String nextStep;
}