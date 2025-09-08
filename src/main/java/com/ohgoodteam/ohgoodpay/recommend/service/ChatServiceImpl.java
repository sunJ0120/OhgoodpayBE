package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatCheckHobbyResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatMoodResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatStartResponse;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatUpdateHobbyResponse;
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
        String nextStep = "mood_input";

        return ChatStartResponse.builder()
                .message(greetingMessage)
                .nextStep(nextStep)
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
                .nextStep(nextStep)
                .build();
    }

    /**
     * 고객 아이디 입력받아서 llm요청
     * 고객 유효성 검증 → 고객명 조회 → 취미 확인하는 메시지 생성
     */
    @Override
    @Transactional(readOnly = true)
    public ChatCheckHobbyResponse checkHobby(Long customerId) {
        validateCustomerId(customerId);
        // 사용자가 이미 가지고 있는 hobby data 가져오기
        String hobby = customerRepository.findByCustomerId(customerId)
                .map(CustomerEntity::getHobby)
                .orElse(null);

        // 취미 정보가 없는 경우 처리
        if (hobby == null || hobby.trim().isEmpty()) {
            throw new IllegalArgumentException("취미 정보가 등록되지 않은 고객입니다");
        }
        
        // TODO: Redis 캐싱 구현 (hobby만 저장)

        // TODO: FastAPI 연동 구현
        String llmMessage = String.format("평소 관심있던 %s로 뭔가 찾아볼까?", hobby);
        String nextStep = "hobby_confirm";

        return ChatCheckHobbyResponse.builder()
                .message(llmMessage)
                .currentHobbies(hobby)
                .nextStep(nextStep)
                .build();
    }

    /**
     * 고객 취미 업데이트
     * 고객 유효성 검증 → DB 업데이트 → 응답 생성
     */
    @Override
    public ChatUpdateHobbyResponse updateHobby(Long customerId, String newHobby) {
        validateCustomerId(customerId);
        // TODO: 취미도 검증로직 넣을지는 고민중

        // TODO: Redis 캐싱 구현 (hobby만 저장)
        
        // DB 업데이트 실행
        int updatedRows = customerRepository.updateHobbyByCustomerId(customerId, newHobby);
        
        if (updatedRows == 0) {
            throw new IllegalStateException("취미 업데이트에 실패했습니다");
        }

        // TODO: FastAPI 연동 구현
        String responseMessage = String.format("%s에 관심생겼구나! 좋은 선택이야~", newHobby);
        String nextStep = "analyzing_purchases";

        return ChatUpdateHobbyResponse.builder()
                .message(responseMessage)
                .updatedHobby(newHobby)
                .nextStep(nextStep)
                .build();
    }
}
