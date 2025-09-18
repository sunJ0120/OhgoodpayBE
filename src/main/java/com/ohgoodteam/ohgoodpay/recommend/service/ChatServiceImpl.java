package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.CustomerContextWrapper;
import com.ohgoodteam.ohgoodpay.recommend.dto.ValidationResult;
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
    public BasicChatResponseDTO chat(Long customerId, String sessionId, String message) {
        log.info("세션 아이디와 고객 아이디 체크 sessionId: {} for customerId: {}", sessionId, customerId);

        // 0. flow를 redis에서 가져오기
        String currentFlow = chatCacheService.getFlowBySession(sessionId);

        // start 플로우는 유효성 검증 없이 바로 LLM 처리
        if (currentFlow.equals("start")) {
            // 2. 플로우 전환 및 전처리
            String nextFlow = flowService.getNextFlow(currentFlow);
            chatCacheService.saveCntBySession(sessionId, 1); //count 초기화
            chatCacheService.saveFlowBySession(sessionId, nextFlow); //nextflow 초기화
            log.info("캐싱되어 있는 바뀐 플로우 확인하기 : {}", chatCacheService.getFlowBySession(sessionId));

            // 3. 캐싱된 데이터 수집
            CustomerContextWrapper context = collectCachedData(customerId, sessionId);
            // 4. LLM 요청
            BasicChatResponseDTO response = requestToLLM(sessionId, context, message, nextFlow);
            // 5. 응답 후처리
            processAfterResponse(response, customerId, sessionId, nextFlow);
            return response;
        }

        // 일반 플로우는 유효성 검증 후 처리
        ValidInputResponseDTO validated = validCheckService.validateInput(customerId, sessionId, message, currentFlow);
        ValidationResult validationResult = processValidationResult(validated, customerId, sessionId, currentFlow);
        if (validationResult.shouldReturn()) { //바로 리턴 해야 한다면, 플로우 변환 없이 처리
            return validationResult.getResponse();
        }

        // 2. 플로우 전환 및 전처리
        String nextFlow = flowService.getNextFlow(currentFlow);
        chatCacheService.saveCntBySession(sessionId, 1); //count 초기화
        chatCacheService.saveFlowBySession(sessionId, nextFlow); //nextflow 초기화
        log.info("캐싱되어 있는 바뀐 플로우 확인하기 : {}", chatCacheService.getFlowBySession(sessionId));

        if (validated.isValid()) {
            saveFlowSpecificData(customerId, sessionId, nextFlow, validated);
        }
        // 3. 캐싱된 데이터 수집
        CustomerContextWrapper context = collectCachedData(customerId, sessionId);
        // 4. LLM 요청
        BasicChatResponseDTO response = requestToLLM(sessionId, context, message, nextFlow);

        // + currentFlow가 recommendation이었는데, nextFlow가 recommendation일 경우, products 캐싱
        if(response.getFlow().equals("recommendation")){
            chatCacheService.saveProductsBySession(sessionId, response.getProducts());

            // 3개 제한된 새 응답 객체 생성
            response = BasicChatResponseDTO.builder()
                    .sessionId(response.getSessionId())
                    .message(response.getMessage())
                    .newHobby(response.getNewHobby())
                    .products(chatCacheService.getProductsBySession(sessionId)) // 3개 제한
                    .summary(response.getSummary())
                    .flow(response.getFlow())
                    .build();

            // TODO : 캐싱 할 상품 모자랄 경우에 대한 해결책 및 로직 정의 필요.
        }

        // 5. 응답 후처리, 강제로 넘어간 경우는 저장 무시한다.
        if (validated.isValid()) {
            processAfterResponse(response, customerId, sessionId, nextFlow);
        }
        return response;
    }

    /**
     * 검증 결과 처리 및 재시도 로직
     */
    private ValidationResult processValidationResult(ValidInputResponseDTO validated, Long customerId, String sessionId, String currentFlow) {
        if (!validated.isValid()) {
            int cnt = chatCacheService.getCntBySession(sessionId);
            // 두 번 이하로 유효하지 않은 응답이 들어왔을경우, 다시 같은 플로우를 실행
            if (cnt < 2) {
                chatCacheService.saveCntBySession(sessionId, cnt + 1);
                BasicChatResponseDTO errorResponse = BasicChatResponseDTO.builder()
                        .sessionId(sessionId)
                        .message(validated.getMessage())
                        .newHobby("")
                        .products(null)
                        .build();
                return ValidationResult.returnResponse(errorResponse);
            } else if(cnt == 2) {
                // cnt == 2면 다음 플로우로 강제 진행하고 LLM 요청 계속 진행
                log.info("Validation 실패 {}번, 다음 플로우로 넘어가서 LLM 요청 진행", cnt);
                return ValidationResult.continueFlow();
            }
        }
        log.info("------------isValid True로 인해 플로우를 그대로 이어나간다.----------");
        return ValidationResult.continueFlow();
    }

    /**
     * 캐싱된 데이터 수집
     */
    private CustomerContextWrapper collectCachedData(Long customerId, String sessionId) {
        return CustomerContextWrapper.builder()
                .customerInfo(chatCacheService.getCustomerInfo(customerId))
                .hobby(chatCacheService.getHobby(customerId))
                .mood(chatCacheService.getMoodBySession(sessionId))
                .balance(chatCacheService.getBalance(customerId))
                .summary(chatCacheService.getSummaryBySession(sessionId))
                .build();
    }

    /**
     * 플로우별 특정 데이터 저장
     */
    private void saveFlowSpecificData(Long customerId, String sessionId, String nextFlow, ValidInputResponseDTO validated) {
        // 플로우 전환시 카운트 리셋
        // TODO : 이거 0으로 만들고 세는 방향으로 가야할 것 같아서 고민중
        chatCacheService.saveCntBySession(sessionId, 1);
        // 특정 플로우에서 mood 저장, 유효한 경우엔 캐싱하고 아닌 경우엔 저장 없이 플로우만 넘기도록 한다.
        if ("hobby_check".equals(nextFlow) && validated != null) {
            chatCacheService.saveMoodBySession(sessionId, validated.getInputMessage());
        }
        // 특정 플로우에서 hobby 저장
        if ("choose".equals(nextFlow) && validated != null) {
            chatCacheService.saveHobby(customerId, validated.getInputMessage());
        }
    }

    /**
     * LLM 요청 처리
     */
    private BasicChatResponseDTO requestToLLM(String sessionId, CustomerContextWrapper context, String message, String nextFlow) {
        return llmService.generateChat(
                sessionId,
                context.getCustomerInfo(),
                context.getMood(),
                context.getHobby(),
                context.getBalance(),
                message,
                context.getSummary(),
                nextFlow
        );
    }

    /**
     * 응답 후처리 (DB 업데이트, 캐싱)
     */
    private void processAfterResponse(BasicChatResponseDTO response, Long customerId, String sessionId, String nextFlow) {
        // DB 업데이트
        if (!response.getNewHobby().equals("")) {
            customerRepository.updateHobbyByCustomerId(customerId, response.getNewHobby());
            log.info("취미 DB 업데이트 완료 - customerId: {}, hobby: {}", customerId, response.getNewHobby());
        }
        // 캐싱
        // 요약본 저장
        if (response.getSummary() != null && !response.getSummary().isEmpty()) {
            chatCacheService.saveSummaryBySession(sessionId, response.getSummary());
        }
    }
}
