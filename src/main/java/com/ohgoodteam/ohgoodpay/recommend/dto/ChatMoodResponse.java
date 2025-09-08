package com.ohgoodteam.ohgoodpay.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class ChatMoodResponse {
    private final String message;
    private final String step;
}
