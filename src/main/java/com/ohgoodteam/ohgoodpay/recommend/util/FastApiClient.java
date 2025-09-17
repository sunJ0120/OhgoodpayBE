package com.ohgoodteam.ohgoodpay.recommend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * FastAPI 서버와 통신하는 클라이언트
 */
@Component
public class FastApiClient {
    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    private final RestTemplate restTemplate;

    public FastApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public <T, R> R post(String endpoint, T request, Class<R> responseType) {
        try {
            String url = fastApiBaseUrl + endpoint;

            // FastApiClient.java의 post 메서드에 로그 추가
            System.out.println("FastAPI Base URL: " + fastApiBaseUrl);
            System.out.println("Endpoint: " + endpoint);
            System.out.println("Full URL: " + url);
            System.out.println("Request body: " + request.toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<T> entity = new HttpEntity<>(request, headers);

            // RestTemplate이 JSON을 BasicChatResponse로 자동 변환해서 우리 responseType에 맞게 반환해준다.
            ResponseEntity<R> response = restTemplate.postForEntity(url, entity, responseType);

            // response.getBody()로 실제 객체 추출
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 실패: " + e.getMessage());
        }
    }
}
