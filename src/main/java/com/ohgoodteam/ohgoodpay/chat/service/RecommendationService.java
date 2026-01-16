package com.ohgoodteam.ohgoodpay.chat.service;

import com.ohgoodteam.ohgoodpay.chat.dto.ChatMessage;
import com.ohgoodteam.ohgoodpay.chat.dto.ChatResponse;
import com.ohgoodteam.ohgoodpay.chat.dto.ProductDto;
import com.ohgoodteam.ohgoodpay.chat.exception.LlmServerException;
import com.ohgoodteam.ohgoodpay.chat.util.LlmApiClient;
import com.ohgoodteam.ohgoodpay.chat.util.PromptProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ohgoodteam.ohgoodpay.chat.util.PromptProvider.getKeywordPrompt;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {
    private static final String SEARCH_KEYWORD_PREFIX = "SEARCH_KEYWORD:";
    private static final String MAX_PRICE_PREFIX = "MAX_PRICE:";
    private static final int MAX_RETRY_COUNT = 2;

    private final LlmApiClient llmApiClient;
    private final PromptProvider promptProvider;
    private final ProductService productService;

    public ChatResponse chat(String sessionId, List<ChatMessage> history, String userMessage, String userName) {
        String systemPrompt = promptProvider.getBasePrompt(userName);

        String response = llmApiClient.chat(history, userMessage, systemPrompt);

        if (response.contains(SEARCH_KEYWORD_PREFIX)) {
            String keyword = extractKeyword(response);
            Integer maxPrice = extractMaxPrice(response);
            String cleanMessage = extractCleanMessage(response);

            if (keyword.isEmpty()) {
                return createRetryResponse(sessionId);
            }

            // 1차 : 원본 키워드
            List<ProductDto> products = productService.searchAndCache(keyword, maxPrice);

            // 2차 : 단순 키워드
            if (products.isEmpty()) {
                String simpleKeyword = simplifyKeyword(keyword);
                products = productService.searchAndCache(simpleKeyword, maxPrice);
            }

            // 3차 : LLM에게 키워드 재생성 요청 (최대 2회)
            if (products.isEmpty()) {
                products = retryWithNewKeyword(keyword, maxPrice);
            }

            // 4차 : 최종 실패
            if (products.isEmpty()) {
                return createFailResponse(sessionId, cleanMessage);
            }

            return new ChatResponse(sessionId, cleanMessage, products);
        }
        return new ChatResponse(sessionId, response, List.of());
    }

    private String extractKeyword(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains(SEARCH_KEYWORD_PREFIX)) {
                int idx = line.indexOf(SEARCH_KEYWORD_PREFIX);
                return line.substring(idx + SEARCH_KEYWORD_PREFIX.length()).trim();
            }
        }
        return "";    // 키워드가 없을 경우 빈칸
    }

    private String extractCleanMessage(String text) {
        return text.split(SEARCH_KEYWORD_PREFIX)[0].trim();
    }

    private Integer extractMaxPrice(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains(MAX_PRICE_PREFIX)) {
                try {
                    int idx = line.indexOf(MAX_PRICE_PREFIX);
                    String value = line.substring(idx + MAX_PRICE_PREFIX.length()).trim();
                    if (value.isEmpty()) return null;
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private String simplifyKeyword(String keyword) {
        String[] words = keyword.split(" ");
        if (words.length > 2) {
            return words[0] + " " + words[words.length - 1];    // 앞 뒤 두 개만 붙여서 간단한 키워드 생성
        }
        return keyword;
    }

    private ChatResponse createRetryResponse(String sessionId) {
        return new ChatResponse(
                sessionId,
                "앗, 잠깐 혼선이 생겼어! 😅 다시 한 번 어떤 상품 찾는지 말해줄래?",
                List.of()
        );
    }

    private ChatResponse createFailResponse(String sessionId, String cleanMessage) {
        return new ChatResponse(
                sessionId,
                cleanMessage + "\n\n근데 아쉽게도 조건에 딱 맞는 상품을 못 찾았어 😢 다른 키워드나 예산으로 다시 얘기해줄래?",
                List.of()
        );
    }

    private List<ProductDto> retryWithNewKeyword(String failedKeyword, Integer maxPrice) {
        for (int i = 0; i < MAX_RETRY_COUNT; i++) {
            String newKeyword = requestNewKeyword(failedKeyword, i);

            if (newKeyword == null || newKeyword.isEmpty()) {
                continue;
            }

            List<ProductDto> products = productService.searchAndCache(newKeyword, maxPrice);
            if (!products.isEmpty()) {
                return products;
            }

            failedKeyword = newKeyword;
        }

        return List.of();    // 두 번 돌아도 없는 경우
    }

    private String requestNewKeyword(String failedKeyword, int attemptCount) {
        String prompt = getKeywordPrompt(failedKeyword);

        try {
            String response = llmApiClient.chat(List.of(), prompt, "너는 검색 키워드 생성 전문가야");
            return response.trim();
        } catch (LlmServerException e) {
            log.warn("키워드 재생성 실패 ({}회차): {}", attemptCount, e.getMessage());
            return null;
        }
    }
}
