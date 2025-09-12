package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto.ProductDTO;
import com.ohgoodteam.ohgoodpay.recommend.util.CacheSpec;
import com.ohgoodteam.ohgoodpay.recommend.util.CacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 고객 정보 캐싱 서비스 (REDIS)
 *
 * CacheStore Read-through 패턴 사용
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatCacheService {
    // redis 사용을 위한 CacheStore 주입
    private final CacheStore cacheStore;
    private final CustomerRepository customerRepository;

    // 고객 기본 정보 조회 (Read-through)
    public CustomerCacheDTO getCustomerInfo(Long customerId) {
        return cacheStore.getOrLoad(
                CacheSpec.CUSTOMER,
                customerId,
                CustomerCacheDTO.class,
                () -> customerRepository.findCustomerCacheInfoById(customerId)
        );
    }

    // 고객 취미 정보 조회 (Read-through)
    public String getHobby(Long customerId) {
        // Read-through 패턴: Cache Miss 시 자동으로 DB 조회 후 캐시 저장
        // 취미 정보가 없을 경우 "취미 없음" 기본값 반환, 차후 정보를 받도록 한다.
        return cacheStore.getOrLoad(
                CacheSpec.HOBBY,
                customerId,
                String.class,
                () -> customerRepository.findHobbyByCustomerId(customerId)
                        .orElse("취미 없음")
        );
    }

    // 고객 기분 저장값 조회
    // 캐싱 안 되어 있을 경우 "보통" 기본값 반환
    public String getMood(Long customerId) {
        String mood = cacheStore.get(CacheSpec.MOOD, customerId, String.class);
        return mood != null ? mood : "보통"; // 기본값
    }

    // 고객 잔액 조회 (Read-through)
    // 잔액 정보가 없을 경우 50000원 기본값 반환 (일단은...)
    public Integer getBalance(Long customerId) {
        return cacheStore.getOrLoad(
                CacheSpec.BALANCE,
                customerId,
                Integer.class,
                () -> customerRepository.findBalanceByCustomerId(customerId)
                        .orElse(50000)
        );
    }

//    // 고객 최근 구매 카테고리 조회 (Read-through)
//    // TODO : 카테고리 테이블 필요.. 일단은 임시 데이터로 진행
//    public String getRecentPurchaseCategory(Long customerId) {
//        // return cacheStore.get(CacheSpec.RECENT_PURCHASE, customerId);
//        return "운동 기구";
//    }

    // FastAPI에서 캐시 저장을 담당하므로, Spring에서는 저장 메서드 없음
    // Spring은 DB 저장만 담당 (CustomerRepository 사용)
}