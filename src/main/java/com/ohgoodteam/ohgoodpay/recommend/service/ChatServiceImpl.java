package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatMoodResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatStartResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final CustomerRepository customerRepository;

    /**
     * 고객 ID 유효성 검증
     *
     * 채팅에서 이를 반복적으로 사용하기 때문에 따로 별도의 메서드로 분리하였다.
     */
    private void validateCustomerId(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 고객 ID입니다");
        }
    }

    /**
     * 고객명 조회 및 유효성 검증
     *
     * 채팅에서 이를 반복적으로 사용하기 때문에 따로 별도의 메서드로 분리하였다.
     */
    private String getValidCustomerName(Long customerId) {
        String name = customerRepository.findByCustomerId(customerId)
                .map(CustomerEntity::getName)
                .orElse(null);
        
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 고객입니다");
        }
        
        return name;
    }

    /**
     * 채팅 시작 처리
     * 고객 유효성 검증 → 고객명 조회 → 인사 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatStartResponse startChat(Long customerId) {
        validateCustomerId(customerId);
        String name = getValidCustomerName(customerId);

        // TODO: Redis 캐싱 구현 (CustomerCacheDto 사용)
        
        // TODO: FastAPI 연동 구현
        String greetingMessage = String.format("안녕 나는 너만의 오레이봇봇 ~ 나를 레이라고 불러줘 %s~ 오늘 기분은 어때?", name);

        return ChatStartResponse.builder()
                .message(greetingMessage)
                .build();
    }

    /**
     * 고객 기분 입력받아서 llm요청
     * 고객 유효성 검증 → 고객명 조회 → 기분 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatMoodResponse moodChat(Long customerId, String mood) {
        validateCustomerId(customerId);
        String name = getValidCustomerName(customerId);

        // TODO: Redis 캐싱 구현 (mood만 저장)

        // TODO: FastAPI 연동 구현
        String greetingMessage = String.format("%s이가 기분이 %s하다니 나도 좋은걸~ 그럼 오늘 뭐가 필요한지 알아볼까?", name, mood);
        String nextStep = "hobby_check";

        return ChatMoodResponse.builder()
                .message(greetingMessage)
                .step(nextStep)
                .build();
    }
}
