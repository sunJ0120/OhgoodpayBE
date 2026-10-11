package com.ohgoodteam.ohgoodpay.chat.service;

import com.ohgoodteam.ohgoodpay.chat.dto.ChatResponse;
import com.ohgoodteam.ohgoodpay.chat.dto.ProductDto;
import com.ohgoodteam.ohgoodpay.chat.exception.LlmServerException;
import com.ohgoodteam.ohgoodpay.chat.flow.ChatFlow;
import com.ohgoodteam.ohgoodpay.chat.session.ChatSessionStore;
import com.ohgoodteam.ohgoodpay.common.dto.CustomerCacheDTO;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.llm.client.MlChatClient;
import com.ohgoodteam.ohgoodpay.llm.dto.*;
import com.ohgoodteam.ohgoodpay.llm.service.LlmCallLogService;
import com.ohgoodteam.ohgoodpay.llm.service.PromptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 추천 대화 흐름(상태기계). 단계 전이·재시도·세션 상태는 BE 가 맡고, LLM 호출은 ML(/ml/chat)에 위임한다.
 *
 * - 사용자가 지금 답하는 단계(current)의 입력을 먼저 검증한다 (validate_* 프롬프트).
 * - 유효하면 다음 단계(next)의 질문을 ML 에서 받는다. 무효면 최대 2번까지 같은 단계에 머문다.
 * - 프롬프트는 DB 의 활성 버전을 요청에 실어 보내고, 응답의 호출 메타데이터는 llm_call_log 에 남긴다.
 * - LLM 호출이 실패하면 상태를 진행하지 않고 errorCode 를 담은 LlmServerException 을 던진다.
 *   (타임아웃·폴백 정책은 후속 작업 ②에서)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private static final int MAX_DISPLAY_CNT = 3;
    private static final int MAX_INVALID_TRIES = 2;
    private static final int DEFAULT_BALANCE = 50_000;
    private static final String NO_HOBBY = "취미 없음";

    private final ChatSessionStore store;
    private final CustomerRepository customers;
    private final MlChatClient ml;
    private final PromptService prompts;
    private final LlmCallLogService callLogs;

    public ChatResponse chat(Long customerId, String sessionId, String message) {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId 가 필요합니다");
        }
        CustomerCacheDTO customer = customers.findCustomerCacheInfoById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("존재하지 않는 고객입니다: " + customerId);
        }

        ChatFlow current = store.getFlow(sessionId);
        if (current == ChatFlow.START) {
            return start(customer, sessionId);
        }

        // 1) 지금 단계의 입력 검증
        MlValidationResponse validation = ml.validate(new MlValidationRequest(
                customerId, sessionId, message, current.getValue(),
                new MlPrompts(prompts.getActive(current.validationPromptKey()), null)));
        callLogs.record(sessionId, current.getValue(), validation.calls());
        failIfMainCallFailed(validation.calls());

        if (!validation.isValid()) {
            return handleInvalid(customer, sessionId, current, validation.message());
        }

        // 2) 유효한 입력 저장
        String hobby = currentHobby(customerId);
        if (current == ChatFlow.MOOD_CHECK && hasText(validation.normalized())) {
            store.saveMood(sessionId, validation.normalized());
        }
        if (current == ChatFlow.HOBBY_CHECK && hasHobby(validation.normalized())) {
            customers.updateHobbyByCustomerId(customerId, validation.normalized());
            hobby = validation.normalized();
        }

        // 3) 다음 단계 질문 생성
        return askNext(customer, sessionId, current.next(), message, hobby);
    }

    // 세션 종료
    public void clearSession(String sessionId) {
        store.clear(sessionId);
    }

    /** 첫 진입: mood_check 질문은 LLM 없이 고정 문장으로 낸다. */
    private ChatResponse start(CustomerCacheDTO customer, String sessionId) {
        store.saveFlow(sessionId, ChatFlow.MOOD_CHECK);
        store.saveCount(sessionId, 1);
        return new ChatResponse(sessionId, ChatMessages.moodQuestion(customer.getName()), List.of());
    }

    private ChatResponse handleInvalid(CustomerCacheDTO customer, String sessionId, ChatFlow current, String message) {
        int tries = store.getCount(sessionId);
        if (tries < MAX_INVALID_TRIES) {
            store.saveCount(sessionId, tries + 1);
            return new ChatResponse(sessionId, message, List.of());
        }
        // 2번 실패하면 입력을 무시하고 다음 단계로 강제 진행
        log.info("검증 {}회 실패, 다음 단계로 진행: session={}, flow={}", tries, sessionId, current.getValue());
        return askNext(customer, sessionId, current.next(), "", currentHobby(customer.getCustomerId()));
    }

    private ChatResponse askNext(CustomerCacheDTO customer, String sessionId, ChatFlow next, String inputMessage, String hobby) {
        boolean hasHobby = hasHobby(hobby);
        MlChatResponse res = ml.chat(new MlChatRequest(
                sessionId, next.getValue(), inputMessage,
                new MlCustomerInfo(customer.getCustomerId(), customer.getName(), customer.getCreditLimit()),
                store.getMood(sessionId),
                hasHobby ? hobby : "",
                currentBalance(customer.getCustomerId()),
                store.getSummary(sessionId),
                store.getPrevKeyword(sessionId),
                new MlPrompts(prompts.getActive(next.chatPromptKey(hasHobby)), prompts.getActive("summary_update"))));
        callLogs.record(sessionId, next.getValue(), res.calls());
        failIfMainCallFailed(res.calls());
        if (res.message() == null) {
            throw new LlmServerException("ML 응답에 message 가 없습니다", "LLM_ERROR");
        }

        // 성공했을 때만 상태를 진행한다
        if (res.summary() != null) {
            store.saveSummary(sessionId, res.summary());
        }
        if (res.keyword() != null) {
            store.savePrevKeyword(sessionId, res.keyword());
        }
        store.saveFlow(sessionId, next);
        store.saveCount(sessionId, 1);

        return new ChatResponse(sessionId, res.message(), toProducts(res.products()));
    }

    /** main 호출이 실패했으면 ML 이 알려준 errorCode 를 그대로 올린다. (summary 실패는 대화를 막지 않는다) */
    private void failIfMainCallFailed(List<MlCallMeta> calls) {
        if (calls == null || calls.isEmpty()) {
            throw new LlmServerException("ML 응답에 호출 정보가 없습니다", "ML_ERROR");
        }
        calls.stream()
                .filter(c -> "main".equals(c.slot()) && !c.valid())
                .findFirst()
                .ifPresent(c -> {
                    String code = c.errorCode() != null ? c.errorCode() : "LLM_ERROR";
                    throw new LlmServerException("LLM 호출 실패: " + code, code);
                });
    }

    private String currentHobby(Long customerId) {
        return customers.findHobbyByCustomerId(customerId).orElse("");
    }

    private int currentBalance(Long customerId) {
        return customers.findBalanceByCustomerId(customerId).orElse(DEFAULT_BALANCE);
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    private static boolean hasHobby(String hobby) {
        return hasText(hobby) && !NO_HOBBY.equals(hobby.trim());
    }

    private static List<ProductDto> toProducts(List<MlProduct> products) {
        if (products == null) {
            return List.of();
        }
        return products.stream().limit(MAX_DISPLAY_CNT).map(ProductDto::of).toList();
    }
}
