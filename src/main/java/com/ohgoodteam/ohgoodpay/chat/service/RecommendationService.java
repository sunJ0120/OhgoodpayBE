package com.ohgoodteam.ohgoodpay.chat.service;

import com.ohgoodteam.ohgoodpay.chat.dto.ChatMessage;
import com.ohgoodteam.ohgoodpay.chat.dto.LlmResponse;
import com.ohgoodteam.ohgoodpay.chat.dto.ProductDto;
import com.ohgoodteam.ohgoodpay.chat.util.LlmApiClient;
import com.ohgoodteam.ohgoodpay.chat.util.PromptProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {
    private static final String SEARCH_KEYWORD_PREFIX = "SEARCH_KEYWORD:";

    private final LlmApiClient llmApiClient;
    private final PromptProvider promptProvider;
    private final ProductService productService;

    public LlmResponse chat(String sessionId, List<ChatMessage> history, String userMessage, String userName) {
        String systemPrompt = promptProvider.getBasePrompt(userName);

        String response = llmApiClient.chat(history, userMessage, systemPrompt);

        if(response.contains(SEARCH_KEYWORD_PREFIX)){
            String keyword = extractKeyword(response);
            String cleanMessage = response.split(SEARCH_KEYWORD_PREFIX)[0].trim();

            List<ProductDto> products = productService.searchAndCache(keyword);

            return new LlmResponse(sessionId, cleanMessage, products);
        }
        return new LlmResponse(sessionId, response, null);
    }

    private String extractKeyword(String text) {
        return text.split(SEARCH_KEYWORD_PREFIX)[1].trim().replaceAll("\"", "");
    }
}
