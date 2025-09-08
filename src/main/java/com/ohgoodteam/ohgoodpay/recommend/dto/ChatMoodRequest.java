package com.ohgoodteam.ohgoodpay.recommend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class ChatMoodRequest {
    private final Long customerId;
    private final String mood;
}
