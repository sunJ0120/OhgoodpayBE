package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDto;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.ProductDto;
import com.ohgoodteam.ohgoodpay.recommend.util.CacheSpec;
import com.ohgoodteam.ohgoodpay.recommend.util.CacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 고객 정보 캐싱 서비스
 * CacheStore Read-through 패턴 사용
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCacheService {
    private final CacheStore cacheStore;
    // private final CacheStore cacheStore; // TODO: CacheStore 주입
    
    /**
     * 고객 기본 정보 조회 (Read-through)
     */
    public CustomerCacheDto getCustomerInfo(Long customerId) {
        // Read-through 패턴: Cache Miss 시 자동으로 DB 조회 후 캐시 저장
        // return cacheStore.get(CacheSpec.CUSTOMER_INFO, customerId);
        
        // TODO: 임시 Mock 데이터 (REDIS / CacheStore 연동 전까지)
        return CustomerCacheDto.builder()
                .customerId(customerId)
                .name("테스트고객" + customerId)
                .creditLimit(200000)
                .build();
    }

    /**
     * 고객 취미 정보 조회 (Read-through)
     */
    public String getHobby(Long customerId) {
        // Read-through 패턴: Cache Miss 시 자동으로 DB 조회 후 캐시 저장
        // return cacheStore.get(CacheSpec.HOBBY, customerId);

        // TODO: 임시 Mock 데이터 (REDIS / CacheStore 연동 전까지)
        return "등산";
    }

    /**
     * 고객 기분 저장값 조회
     */
    public String getMood(Long customerId) {
        // return cacheStore.get(CacheSpec.MOOD, customerId);
        return "행복";
    }
    /**
     * 고객 잔액 조회 (Read-through)
     */
    public Integer getBalance(Long customerId) {
        // return cacheStore.get(CacheSpec.BALANCE, customerId);
        return 40000; // TODO: 임시 Mock 데이터
    }

    /**
     * 고객 최근 구매 카테고리 조회 (Read-through)
     */
    public String getRecentPurchaseCategory(Long customerId) {
        // return cacheStore.get(CacheSpec.RECENT_PURCHASE, customerId);
        
        // TODO: 임시 Mock 데이터
        return "운동 기구";
    }

    /**
     * 추천 결과 캐시 저장
     */
    public void saveRecommendProducts(Long customerId, List<ProductDto> products) {
        // TODO: 임시 Mock 데이터
        // cacheStore.pushList(CacheSpec.RECOMMEND_PRODUCTS, customerId, products);
    }

    /**
     * 추천 결과 조회
     */
    public ProductDto getRecommendProducts(Long customerId, int index) {
//        return cacheStore.getByIndex(CacheSpec.RECOMMEND_PRODUCTS, customerId, index, ProductDto.class);
        
        // TODO: 임시 Mock 데이터 (index별로 다른 상품 반환)
        return ProductDto.builder()
                .rank(index + 1)
                .name("추천상품 " + (index + 1))
                .price(10000 + (index * 5000))
                .image("https://example.com/product" + (index + 1) + ".jpg")
                .url("https://example.com/product/" + (index + 1))
                .category("운동 기구")
                .build();
    }

}