package com.ohgoodteam.ohgoodpay.chat.service;

import com.ohgoodteam.ohgoodpay.chat.dto.ChatMessage;
import com.ohgoodteam.ohgoodpay.chat.dto.ChatResponse;
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
    private static final String MAX_PRICE_PREFIX = "MAX_PRICE:";

    private final LlmApiClient llmApiClient;
    private final PromptProvider promptProvider;
    private final ProductService productService;

    public ChatResponse chat(String sessionId, List<ChatMessage> history, String userMessage, String userName) {
        String systemPrompt = promptProvider.getBasePrompt(userName);

        String response = llmApiClient.chat(history, userMessage, systemPrompt);

        if(response.contains(SEARCH_KEYWORD_PREFIX)){
            String keyword = extractKeyword(response);
            Integer maxPrice = extractMaxPrice(response);
            String cleanMessage = extractCleanMessage(response);

            // 키워드 자체가 없는 경우 : 다시 키워드를 받을 수 있도록 준비
            if (keyword.isEmpty()) {
                log.warn("LLM 응답에서 키워드 추출 실패: {}", response);
                return new ChatResponse(
                        sessionId,
                        "앗, 잠깐 혼선이 생겼어! 😅 다시 한 번 어떤 상품 찾는지 말해줄래?",
                        List.of()
                );
            }

            List<ProductDto> products = productService.searchAndCache(keyword, maxPrice);

            if (products.isEmpty()) {    // 검색 결과가 없을 경우, 단축 키워드로 한 번더 연결
                String simpleKeyword = simplfyKeyword(keyword);
                products = productService.searchAndCache(simpleKeyword, maxPrice);
            }

            // 단축 키워드로 했는데도 결과가 없을 경우, 다른 키워드 받기
            if (products.isEmpty()) {
                return new ChatResponse(
                        sessionId,
                        cleanMessage + "\n\n근데 아쉽게도 조건에 딱 맞는 상품을 못 찾았어 😢 다른 키워드나 예산으로 다시 얘기해줄래?",
                        List.of()
                );
            }

            return new ChatResponse(sessionId, cleanMessage, products);
        }
        return new ChatResponse(sessionId, response, List.of());
    }

    private String extractKeyword(String text) {
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains(SEARCH_KEYWORD_PREFIX)) {
                return line.split(SEARCH_KEYWORD_PREFIX)[1].trim();
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
                    return Integer.parseInt(line.split(MAX_PRICE_PREFIX)[1].trim());    // 뒤에 있는 것을 얻기 위함이다.
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private String simplfyKeyword(String keyword) {
        String[] words = keyword.split(" ");
        if (words.length > 2) {
            return words[0] + " " + words[words.length-1];    // 앞 뒤 두 개만 붙여서 간단한 키워드 생성
        }
        return keyword;
    }
}
