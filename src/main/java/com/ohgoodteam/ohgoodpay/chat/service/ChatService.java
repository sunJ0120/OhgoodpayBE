package com.ohgoodteam.ohgoodpay.chat.service;

import com.ohgoodteam.ohgoodpay.chat.dto.ChatMessage;
import com.ohgoodteam.ohgoodpay.chat.dto.ChatResponse;
import com.ohgoodteam.ohgoodpay.chat.util.CacheSpec;
import com.ohgoodteam.ohgoodpay.chat.util.CacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final RecommendationService recommendationService;
    private final CacheStore cacheStore;

    public ChatResponse chat(String sessionId, String message, String userName) {
        String key = CacheSpec.HISTORY.key(sessionId);

        List<ChatMessage> history = cacheStore.getList(key, ChatMessage.class);
        ChatResponse response = recommendationService.chat(sessionId, history, message, userName);

        history.add(ChatMessage.userContent(message));
        history.add(ChatMessage.assistantContent(response.message()));
        cacheStore.save(key, history, CacheSpec.HISTORY.getTtl());

        return response;
    }

    // 세션 종료
    public void clearSession(String sessionId) {
        cacheStore.delete(CacheSpec.HISTORY.key(sessionId));
    }
}
