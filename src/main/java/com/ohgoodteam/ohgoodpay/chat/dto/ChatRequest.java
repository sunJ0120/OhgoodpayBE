package com.ohgoodteam.ohgoodpay.chat.dto;

public record ChatRequest(
        Long customerId,
        String sessionId,
        String message
) {
}
