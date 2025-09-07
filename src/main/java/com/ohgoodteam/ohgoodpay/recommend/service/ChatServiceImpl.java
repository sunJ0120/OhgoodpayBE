package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatStartResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final CustomerRepository customerRepository;

    /**
     * 채팅 시작 처리
     * 고객 유효성 검증 → 고객명 조회 → 인사 메시지 생성
     */
    @Override
    public ChatStartResponse startChat(Long customerId) {
        // 고객 ID 유효성 검증
        if (customerId == null || customerId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 고객 ID입니다");
        }

        String name = customerRepository.findByCustomerId(customerId)
                .map(CustomerEntity::getName)
                .orElse(null);
        log.info("name 잘 받아오는지 검사하기 : {}", name);
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 고객입니다");
        }

        // TODO: Redis 캐싱 구현 (CustomerCacheDto 사용)
        
        // TODO: FastAPI 연동 구현
        String greetingMessage = String.format("안녕 나는 너만의 오레이봇봇 ~ 나를 레이라고 불러줘 %s~ 오늘 기분은 어때?", name);

        return ChatStartResponse.builder()
                .message(greetingMessage)
                .build();
    }
}
