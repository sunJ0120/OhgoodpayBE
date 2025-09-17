package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.ChatFlowType;
import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;
import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.*;
import com.ohgoodteam.ohgoodpay.recommend.service.fastapi.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 채팅 껍데기 서버 서비스 구현체
 *
 * 고객과의 실시간 채팅, LLM 응답 생성, 추천 서비스 연동을 담당
 * Redis 캐시를 통한 성능 최적화 및 외부 FastAPI 서버와의 통신 처리
 */
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final CustomerRepository customerRepository; // DB 직접 접근용 (update)
    private final ChatCacheService chatCacheService; // 캐싱 서비스
    private final LlmService llmService; // FastAPI LLM 연동 서비스

    /**
     * 채팅 처리
     */
    @Override
    @Transactional(readOnly = true)
    public BasicChatResponseDTO chat(Long customerId, String sessionId, String message, String flow) {
        // TODO: [MVP 이후(~18)] 플로우 틀릴 시, front에서 어떻게 처리할지 정의 필요
        // flow 문자열을 enum으로 변환하여 검증
        ChatFlowType flowType;
        try {
            flowType = ChatFlowType.fromValue(flow);
            log.info("Valid flow type received: {} -> {}", flow, flowType);
        } catch (IllegalArgumentException e) {
            log.error("Invalid flow type received: {}", flow);
            throw new IllegalArgumentException("유효하지 않은 플로우 타입입니다: " + flow);
        }

        // 프론트에서 전달받은 sessionId 사용
        log.debug("Using sessionId: {} for customerId: {}", sessionId, customerId);


        CustomerCacheDTO customerInfo = chatCacheService.getCustomerInfo(customerId); //고객 기본 정보
        String hobby = chatCacheService.getHobby(customerId); //원래 저장되어 있었던 취미
        String mood = chatCacheService.getMoodBySession(sessionId); // 세션별 기분
        Integer balance = chatCacheService.getBalance(customerId);
        String summary = chatCacheService.getSummaryBySession(sessionId); // 세션별 대화 요약

        // LLM 서비스에 채팅 생성 요청 (검증된 enum 값 사용)
        // 속 서버에 요청하기 위한 값들을 겉 서버에서 모아서 넘긴다.
        BasicChatResponseDTO response = llmService.generateChat(
                sessionId,
                customerInfo,
                mood,
                hobby,
                balance,
                message,
                summary,
                flowType.getValue()
        );

        // fast api에서 온 DB 저장 신호 확인
        if (response.isShouldUpdateHobbyDB()) {
            // 신호가 왔다면, spring에서 DB에 저장
            customerRepository.updateHobbyByCustomerId(customerId, response.getNewHobby());
            log.info("취미 DB 업데이트 완료 - customerId: {}, hobby: {}",
                    customerId, response.getNewHobby());
        }

        return response;
    }

}
