package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;
import com.ohgoodteam.ohgoodpay.recommend.util.CacheSpec;
import com.ohgoodteam.ohgoodpay.recommend.util.CacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 고객 정보 캐싱 서비스 (REDIS)
 *
 * CacheStore Read-through 패턴 사용
 * 세션별 관리와 고객별 관리 메서드를 분리하여 세션별로 초기화 해야 하는 정보와 고객별 정보를 명확히 구분
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

    // 고객 취미 정보 저장
    public void saveHobby(Long customerId, String hobby) {
        cacheStore.put(CacheSpec.HOBBY, customerId, hobby);
        log.debug("취미를 customerId: {}로 저장 -> {}", customerId, hobby);
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

    // === SessionId 기반 메서드들 ===

    // 세션별 기분 조회
    public String getMoodBySession(String sessionId) {
        String mood = cacheStore.getBySession(CacheSpec.MOOD, sessionId, String.class);
        return mood != null ? mood : "쏘쏘";
    }

    // 세션별 기분 저장
    public void saveMoodBySession(String sessionId, String mood) {
        cacheStore.saveBySession(CacheSpec.MOOD, sessionId, mood);
    }

    // 세션별 대화 요약 조회
    public String getSummaryBySession(String sessionId) {
        String summary = cacheStore.getBySession(CacheSpec.SUMMARY, sessionId, String.class);
        return summary != null ? summary : ""; // 기본값
    }

    // 세션별 대화 요약 저장
    public void saveSummaryBySession(String sessionId, String summary) {
        cacheStore.saveBySession(CacheSpec.SUMMARY, sessionId, summary);
    }

    // 세션별 플로우 조회
    public String getFlowBySession(String sessionId) {
        String flow = cacheStore.getBySession(CacheSpec.FLOW, sessionId, String.class);
        return flow != null ? flow : "mood_check"; // 플로우 없을 경우 기본값: 첫 번째 플로우
    }

    // 세션별 플로우 저장
    public void saveFlowBySession(String sessionId, String flow) {
        cacheStore.saveBySession(CacheSpec.FLOW, sessionId, flow);
    }

    // 세션별 플로우 count 조회
    public Integer getCntBySession(String sessionId) {
        Integer cnt = cacheStore.getBySession(CacheSpec.COUNT, sessionId, Integer.class);
        return cnt != null ? cnt : 1; // count 없을 경우 기본이 1
    }

    // 세션별 플로우 count 저장
    public void saveCntBySession(String sessionId, Integer count) {
        cacheStore.saveBySession(CacheSpec.COUNT, sessionId, count);
    }
}