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
    private final ValidCheckService validCheckService; // 입력 검증 서비스
    private final FlowService flowService; // 플로우 관리 서비스

    /**
     * 채팅 처리
     */
    @Override
    @Transactional(readOnly = true)
    public BasicChatResponseDTO chat(Long customerId, String sessionId, String message, String flow) {
        log.debug("Using sessionId: {} for customerId: {}", sessionId, customerId);

        // 입력 검증
        ValidInputResponseDTO validated = validCheckService.validateInput(customerId, sessionId, message, flow);

        // 입력이 유효하지 않으면 현재 플로우 유지하고 에러 메시지를 바로 응답.
        if (!validated.isValid()) {
            return BasicChatResponseDTO.builder()
                    .sessionId(sessionId)
                    .message(validated.getMessage())
                    .newHobby("")
                    .products(null)
                    .build();
        }

        // validation 성공시 다음 플로우로 전환
        String nextFlow = flowService.getNextFlow(flow);

        // 입력이 유효한 경우 기존 채팅 생성 로직 수행
        CustomerCacheDTO customerInfo = chatCacheService.getCustomerInfo(customerId);
        String hobby = chatCacheService.getHobby(customerId);
        String mood = chatCacheService.getMoodBySession(sessionId);
        Integer balance = chatCacheService.getBalance(customerId);
        String summary = chatCacheService.getSummaryBySession(sessionId);

        // LLM 서비스에 채팅 생성 요청 (다음 플로우로)
        // TODO : 차후 nextFlow 부분 FRONT에서 제거하고 REDIS로 이관 예정
        BasicChatResponseDTO response = llmService.generateChat(
                sessionId,
                customerInfo,
                mood,
                hobby,
                balance,
                message,
                summary,
                nextFlow
        );

        // fast api에서 newhobby가 왔을 경우 DB 업데이트
        // TODO : 향후 로직 리팩터링시 분리 예정...
        if (!response.getNewHobby().equals("")) {
            customerRepository.updateHobbyByCustomerId(customerId, response.getNewHobby());
            log.info("취미 DB 업데이트 완료 - customerId: {}, hobby: {}", customerId, response.getNewHobby());
        }

        return response;
    }

}
