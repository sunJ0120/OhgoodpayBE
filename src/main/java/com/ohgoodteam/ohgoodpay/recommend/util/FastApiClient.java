package com.ohgoodteam.ohgoodpay.recommend.util;

import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.BasicChatResponseDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.cache.ProductDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FastAPI 서버와 통신하는 클라이언트
 */
@Component
public class FastApiClient {
    @Value("${fastapi.base-url}")
    private String fastApiBaseUrl;

    @Value("${server.port:8081}")
    private String serverPort;

    private final RestTemplate restTemplate;

    public FastApiClient() {
        this.restTemplate = new RestTemplate();
    }

    // post 메서드: 제네릭 타입 T 요청을 받아서 제네릭 타입 R 응답을 반환
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
            R responseBody = response.getBody();

            // BasicChatResponseDTO인 경우 이미지 URL을 프록시 URL로 변환
            if (responseBody instanceof BasicChatResponseDTO) {
                BasicChatResponseDTO chatResponse = (BasicChatResponseDTO) responseBody;
                processImageProxyUrls(chatResponse);
            }

            return responseBody;
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 실패: " + e.getMessage());
        }
    }

    /**
     * BasicChatResponseDTO의 상품 이미지 URL을 프록시 URL로 변환하기 위한 메서드 (CORS 우회)
     */
    private void processImageProxyUrls(BasicChatResponseDTO chatResponse) {
        if (chatResponse.getProducts() != null && !chatResponse.getProducts().isEmpty()) {
            List<ProductDTO> updatedProducts = chatResponse.getProducts().stream()
                .map(this::convertToProxyUrl)
                .collect(Collectors.toList());

            // products 필드를 업데이트 (reflection 또는 builder 패턴 사용)
            try {
                // Reflection을 사용하여 private 필드 수정
                java.lang.reflect.Field productsField = chatResponse.getClass().getDeclaredField("products");
                productsField.setAccessible(true);
                productsField.set(chatResponse, updatedProducts);
            } catch (Exception e) {
                System.err.println("이미지 프록시 URL 변환 실패: " + e.getMessage());
            }
        }
    }

    /**
     * ProductDTO의 이미지 URL을 프록시 URL로 변환
     */
    private ProductDTO convertToProxyUrl(ProductDTO product) {
        if (product.getImage() != null && !product.getImage().isEmpty()) {
            try {
                String proxyUrl = "http://localhost:" + serverPort + "/api/image-proxy?url=" +
                    java.net.URLEncoder.encode(product.getImage(), "UTF-8");

                return ProductDTO.builder()
                    .rank(product.getRank())
                    .name(product.getName())
                    .price(product.getPrice())
                    .image(proxyUrl)
                    .url(product.getUrl())
                    .category(product.getCategory())
                    .build();
            } catch (UnsupportedEncodingException e) {
                System.err.println("URL 인코딩 실패: " + e.getMessage());
                return product;
            }
        }
        return product;
    }
}
