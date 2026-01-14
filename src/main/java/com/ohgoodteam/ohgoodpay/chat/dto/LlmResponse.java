package com.ohgoodteam.ohgoodpay.chat.dto;

import java.util.List;

public record LlmResponse(
        String sessionId,
        String message,
        List<ProductDto> products
) {
}
