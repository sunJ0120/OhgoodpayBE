package com.ohgoodteam.ohgoodpay.chat.service;

import com.ohgoodteam.ohgoodpay.chat.dto.NaverSearchResponse;
import com.ohgoodteam.ohgoodpay.chat.exception.NaverApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NaverShoppingClient {
    private final RestTemplate restTemplate;

    @Value("${naver-search.client-id}")
    private String clientId;

    @Value("${naver-search.client-secret}")
    private String clientSecret;

    private final String baseUrl = "https://openapi.naver.com/v1/search/shop.json";

    public NaverSearchResponse search(String keyword, int display) {
        String apiURL = baseUrl + "?query=" + keyword + "&display=" + display;
        HttpHeaders headers = makeHeader();

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<NaverSearchResponse> response = restTemplate.exchange(
                    apiURL,
                    HttpMethod.GET,
                    entity,
                    NaverSearchResponse.class
            );

            NaverSearchResponse body = response.getBody();
            if (body == null) {
                throw new NaverApiException("빈 응답 수신");
            }

            return body;
        } catch (RestClientException e) {
            log.error("네이버 API 호출 실패: {}", e.getMessage());
            throw new NaverApiException("상품 검색 실패", e);
        }
    }

    private HttpHeaders makeHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        return headers;
    }
}
