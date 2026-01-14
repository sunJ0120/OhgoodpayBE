package com.ohgoodteam.ohgoodpay.chat.dto;

public record ChatMessage (
        String role,
        String content
) {
    public static ChatMessage userContent(String content) {
        return new ChatMessage("user", content);
    }

    public static ChatMessage assistantContent(String content) {
        return new ChatMessage("assistant", content);
    }
}
