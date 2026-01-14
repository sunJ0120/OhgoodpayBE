package com.ohgoodteam.ohgoodpay.chat.dto;

import java.util.List;

public record ChatToLlmRequest(
        List<ChatMessage> history,
        String message
) {}
