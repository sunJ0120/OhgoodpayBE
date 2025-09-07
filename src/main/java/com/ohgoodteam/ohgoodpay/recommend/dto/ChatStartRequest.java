package com.ohgoodteam.ohgoodpay.recommend.dto;

import lombok.*;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class ChatStartRequest {
    private final Long customerId;
}
