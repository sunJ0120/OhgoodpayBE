package com.ohgoodteam.ohgoodpay.chat.dto;

import java.util.List;

public record ChatResponse(
        String sessionId,
        String message,
        List<ProductDto> products  // 없으면 null 또는 빈 리스트
) {
}
