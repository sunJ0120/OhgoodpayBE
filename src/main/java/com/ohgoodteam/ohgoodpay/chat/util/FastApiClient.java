package com.ohgoodteam.ohgoodpay.chat.util;

import com.ohgoodteam.ohgoodpay.chat.dto.ChatMessage;
import com.ohgoodteam.ohgoodpay.chat.dto.ChatToLlmRequest;
import com.ohgoodteam.ohgoodpay.chat.dto.LlmResponse;
import com.ohgoodteam.ohgoodpay.chat.exception.LlmServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FastApiClient {

    private final RestTemplate restTemplate;
    // TODO : 차후 YML 수정 후에 @ConfigurationProperties로 변경 예정
    private final String baseUrl = "http://localhost:8000";

    public LlmResponse chat(List<ChatMessage> history, String message) {
        String url = baseUrl + "/api/chat";

        ChatToLlmRequest request = new ChatToLlmRequest(history, message);
        HttpEntity<ChatToLlmRequest> entity = createRequestEntity(request);

        try {
            // LlmResponse로 자동 변환
            ResponseEntity<LlmResponse> responseEntity = restTemplate.postForEntity(
                    url,
                    entity,
                    LlmResponse.class
            );

            return responseEntity.getBody();
        } catch (RestClientException e) {
            log.error("fast api 호출 실패 : {}", e.getMessage());
            throw new LlmServerException("AI 서버 통신 실패", e);
        }
    }

    private <T> HttpEntity<T> createRequestEntity(T request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(request, headers);
    }
}
