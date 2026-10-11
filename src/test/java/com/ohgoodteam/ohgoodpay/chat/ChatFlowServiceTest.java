package com.ohgoodteam.ohgoodpay.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohgoodteam.ohgoodpay.chat.dto.ChatResponse;
import com.ohgoodteam.ohgoodpay.chat.exception.LlmServerException;
import com.ohgoodteam.ohgoodpay.chat.flow.ChatFlow;
import com.ohgoodteam.ohgoodpay.chat.service.ChatService;
import com.ohgoodteam.ohgoodpay.common.dto.CustomerCacheDTO;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.llm.client.MlChatClient;
import com.ohgoodteam.ohgoodpay.llm.dto.*;
import com.ohgoodteam.ohgoodpay.llm.service.LlmCallLogService;
import com.ohgoodteam.ohgoodpay.llm.service.PromptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatFlowServiceTest {
    static final String SID = "s1";
    static final Long CID = 1L;
    static final ObjectMapper OM = new ObjectMapper();

    @Mock CustomerRepository customers;
    @Mock MlChatClient ml;
    @Mock PromptService prompts;
    @Mock LlmCallLogService logs;

    InMemoryChatSessionStore store = new InMemoryChatSessionStore();
    ChatService service;

    @BeforeEach
    void setUp() {
        service = new ChatService(store, customers, ml, prompts, logs);
        when(customers.findCustomerCacheInfoById(CID)).thenReturn(new CustomerCacheDTO(CID, "오선정", 300_000));
        when(customers.findHobbyByCustomerId(CID)).thenReturn(Optional.of("게임"));
        when(customers.findBalanceByCustomerId(CID)).thenReturn(Optional.of(120_000));
        when(prompts.getActive(anyString())).thenAnswer(inv ->
                new PromptSpecDto(inv.getArgument(0), 3, "content-" + inv.getArgument(0),
                        OM.createObjectNode().put("type", "object"), "gpt-4o-mini"));
    }

    // ---------- helpers ----------
    static MlCallMeta ok(String slot, String key) {
        return new MlCallMeta(slot, key, 3, "gpt-4o-mini", 100, 10, 5, true, null);
    }

    static MlCallMeta fail(String slot, String key, String code) {
        return new MlCallMeta(slot, key, 3, "gpt-4o-mini", 100, 0, 0, false, code);
    }

    void validation(boolean valid, String normalized, String message, MlCallMeta... calls) {
        when(ml.validate(any())).thenReturn(new MlValidationResponse(
                CID, SID, "입력", valid, message, normalized, "x", List.of(calls)));
    }

    void chatOk(String message, String summary, String keyword, List<MlProduct> products) {
        when(ml.chat(any())).thenReturn(new MlChatResponse(
                SID, message, "", products, summary, "x", keyword, null,
                List.of(ok("main", "m"), ok("summary", "summary_update"))));
    }

    static List<MlProduct> products(int n) {
        return java.util.stream.IntStream.rangeClosed(1, n)
                .mapToObj(i -> new MlProduct(i, "상품" + i, 10_000 * i, "img" + i, "url" + i, "cat"))
                .toList();
    }

    MlChatRequest chatRequest() {
        ArgumentCaptor<MlChatRequest> c = ArgumentCaptor.forClass(MlChatRequest.class);
        verify(ml).chat(c.capture());
        return c.getValue();
    }

    MlValidationRequest validationRequest() {
        ArgumentCaptor<MlValidationRequest> c = ArgumentCaptor.forClass(MlValidationRequest.class);
        verify(ml).validate(c.capture());
        return c.getValue();
    }

    // ---------- start: mood_check 는 LLM 없이 고정 문장 (D3) ----------
    @Test
    void start_returnsFixedMoodQuestionWithoutCallingMl() {
        ChatResponse res = service.chat(CID, SID, "안녕");

        assertThat(res.message()).contains("오선정").contains("기분");
        assertThat(res.products()).isEmpty();
        verifyNoInteractions(ml, logs);
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.MOOD_CHECK);
        assertThat(store.getCount(SID)).isEqualTo(1);
    }

    // ---------- 정상 진행 ----------
    @Test
    void moodCheck_valid_savesNormalizedMood_andAsksHobbyWithActivePrompts() {
        store.saveFlow(SID, ChatFlow.MOOD_CHECK);
        validation(true, "positive", "", ok("main", "validate_mood"));
        chatOk("취미 뭐야?", "새 요약", null, List.of());

        ChatResponse res = service.chat(CID, SID, "기분 좋아");

        MlValidationRequest vr = validationRequest();
        assertThat(vr.flow()).isEqualTo("mood_check");
        assertThat(vr.inputMessage()).isEqualTo("기분 좋아");
        assertThat(vr.prompts().main().promptKey()).isEqualTo("validate_mood");
        assertThat(vr.prompts().main().promptVersion()).isEqualTo(3);

        MlChatRequest cr = chatRequest();
        assertThat(cr.flow()).isEqualTo("hobby_check");
        assertThat(cr.mood()).isEqualTo("positive");
        assertThat(cr.hobby()).isEqualTo("게임");
        assertThat(cr.balance()).isEqualTo(120_000);
        assertThat(cr.customerInfo().name()).isEqualTo("오선정");
        assertThat(cr.prompts().main().promptKey()).isEqualTo("chat_hobby_check_with_hobby");
        assertThat(cr.prompts().summary().promptKey()).isEqualTo("summary_update");

        assertThat(res.message()).isEqualTo("취미 뭐야?");
        assertThat(store.getMood(SID)).isEqualTo("positive");
        assertThat(store.getSummary(SID)).isEqualTo("새 요약");
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.HOBBY_CHECK);
        verify(logs).record(eq(SID), eq("mood_check"), any());
        verify(logs).record(eq(SID), eq("hobby_check"), any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"취미 없음", "  "})
    void hobbyQuestion_usesEmptyPromptWhenCustomerHasNoHobby(String hobby) {
        store.saveFlow(SID, ChatFlow.MOOD_CHECK);
        when(customers.findHobbyByCustomerId(CID)).thenReturn(Optional.ofNullable(hobby));
        validation(true, "neutral", "", ok("main", "validate_mood"));
        chatOk("요즘 뭐에 관심 있어?", "s", null, List.of());

        service.chat(CID, SID, "그냥 그래");

        assertThat(chatRequest().prompts().main().promptKey()).isEqualTo("chat_hobby_check_empty");
    }

    @Test
    void hobbyCheck_valid_savesNormalizedHobby_andMovesToChoose() {
        store.saveFlow(SID, ChatFlow.HOBBY_CHECK);
        validation(true, "넷플", "", ok("main", "validate_hobby"));
        chatOk("오 좋다 🎁 vs 📊", "s", null, List.of());

        service.chat(CID, SID, "요즘 퇴근하고 넷플 보는게 낙이야");

        verify(customers).updateHobbyByCustomerId(CID, "넷플");
        MlChatRequest cr = chatRequest();
        assertThat(cr.flow()).isEqualTo("choose");
        assertThat(cr.hobby()).isEqualTo("넷플");
        assertThat(cr.prompts().main().promptKey()).isEqualTo("chat_choose");
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.CHOOSE);
    }

    @Test
    void hobbyCheck_noHobbyAnswer_doesNotOverwriteStoredHobby() {
        store.saveFlow(SID, ChatFlow.HOBBY_CHECK);
        validation(true, "취미 없음", "", ok("main", "validate_hobby"));
        chatOk("m", "s", null, List.of());

        service.chat(CID, SID, "없어");

        verify(customers, never()).updateHobbyByCustomerId(any(), any());
    }

    @Test
    void choose_toRecommendation_mapsAtMostThreeProducts_andRemembersKeyword() {
        store.saveFlow(SID, ChatFlow.CHOOSE);
        validation(true, "recommend", "", ok("main", "validate_choose"));
        chatOk("", "s", "러닝화", products(5));

        ChatResponse res = service.chat(CID, SID, "추천해줘");

        MlChatRequest cr = chatRequest();
        assertThat(cr.flow()).isEqualTo("recommendation");
        assertThat(cr.prompts().main().promptKey()).isEqualTo("keyword_extract");
        assertThat(cr.prevKeyword()).isNull();

        assertThat(res.products()).hasSize(3);
        assertThat(res.products().get(1).name()).isEqualTo("상품2");
        assertThat(res.products().get(1).lprice()).isEqualTo(20_000);
        assertThat(res.products().get(1).image()).isEqualTo("img2");
        assertThat(store.getPrevKeyword(SID)).isEqualTo("러닝화");
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.RECOMMENDATION);
    }

    @Test
    void recommendation_toReRecommendation_sendsPrevKeywordFromSession_notFromSummary() {
        store.saveFlow(SID, ChatFlow.RECOMMENDATION);
        store.savePrevKeyword(SID, "러닝화");
        store.saveSummary(SID, "요약 끝에 키워드: 다른거");
        validation(true, "other", "", ok("main", "validate_recommendation"));
        chatOk("", "s", "트레일화", products(3));

        service.chat(CID, SID, "다른 거 보여줘");

        MlChatRequest cr = chatRequest();
        assertThat(cr.flow()).isEqualTo("re-recommendation");
        assertThat(cr.prevKeyword()).isEqualTo("러닝화");
        assertThat(store.getPrevKeyword(SID)).isEqualTo("트레일화");
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.RE_RECOMMENDATION);
    }

    @Test
    void reRecommendation_staysInReRecommendation() {
        store.saveFlow(SID, ChatFlow.RE_RECOMMENDATION);
        validation(true, "other", "", ok("main", "validate_re_recommendation"));
        chatOk("", "s", "k", products(1));

        service.chat(CID, SID, "또 다른 거");

        assertThat(chatRequest().flow()).isEqualTo("re-recommendation");
        assertThat(validationRequest().prompts().main().promptKey()).isEqualTo("validate_re_recommendation");
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.RE_RECOMMENDATION);
    }

    // ---------- 검증 실패(사용자 입력 탓) ----------
    @Test
    void invalidInput_firstTime_returnsValidationMessage_andKeepsFlow() {
        store.saveFlow(SID, ChatFlow.MOOD_CHECK);
        validation(false, "", "기분을 파악하지 못했어ㅠㅠ 다시 입력해줘!", ok("main", "validate_mood"));

        ChatResponse res = service.chat(CID, SID, "asdf");

        assertThat(res.message()).isEqualTo("기분을 파악하지 못했어ㅠㅠ 다시 입력해줘!");
        verify(ml, never()).chat(any());
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.MOOD_CHECK);
        assertThat(store.getCount(SID)).isEqualTo(2);
        verify(logs).record(eq(SID), eq("mood_check"), any());
    }

    @Test
    void invalidInput_secondTime_forcesNextFlowWithoutUserInput() {
        store.saveFlow(SID, ChatFlow.MOOD_CHECK);
        store.saveCount(SID, 2);
        validation(false, "", "기분을 파악하지 못했어ㅠㅠ 다시 입력해줘!", ok("main", "validate_mood"));
        chatOk("취미 뭐야?", "s", null, List.of());

        ChatResponse res = service.chat(CID, SID, "asdf");

        MlChatRequest cr = chatRequest();
        assertThat(cr.flow()).isEqualTo("hobby_check");
        assertThat(cr.inputMessage()).isEmpty();
        assertThat(res.message()).isEqualTo("취미 뭐야?");
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.HOBBY_CHECK);
        assertThat(store.getCount(SID)).isEqualTo(1);
        assertThat(store.getMood(SID)).isEmpty();      // 무효 입력은 저장하지 않는다
    }

    // ---------- LLM 실패: 오류 코드를 그대로 올리고 상태는 진행하지 않는다 ----------
    @Test
    void validationCallFailure_throwsWithErrorCode_logsIt_andKeepsState() {
        store.saveFlow(SID, ChatFlow.MOOD_CHECK);
        validation(false, "", "", fail("main", "validate_mood", "TIMEOUT"));

        assertThatThrownBy(() -> service.chat(CID, SID, "기분 좋아"))
                .isInstanceOf(LlmServerException.class)
                .hasFieldOrPropertyWithValue("errorCode", "TIMEOUT");

        verify(logs).record(eq(SID), eq("mood_check"), argThat(l -> l.size() == 1 && !l.get(0).valid()
                && "TIMEOUT".equals(l.get(0).errorCode())));
        verify(ml, never()).chat(any());
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.MOOD_CHECK);
        assertThat(store.getCount(SID)).isEqualTo(1);
    }

    @Test
    void chatMainFailure_throwsWithErrorCode_logsIt_andDoesNotAdvance() {
        store.saveFlow(SID, ChatFlow.MOOD_CHECK);
        validation(true, "positive", "", ok("main", "validate_mood"));
        when(ml.chat(any())).thenReturn(new MlChatResponse(SID, null, "", List.of(), "old", "x", null, null,
                List.of(fail("main", "chat_hobby_check_with_hobby", "SCHEMA_INVALID"))));

        assertThatThrownBy(() -> service.chat(CID, SID, "기분 좋아"))
                .isInstanceOf(LlmServerException.class)
                .hasFieldOrPropertyWithValue("errorCode", "SCHEMA_INVALID");

        verify(logs).record(eq(SID), eq("hobby_check"), argThat(l -> l.size() == 1 && "SCHEMA_INVALID".equals(l.get(0).errorCode())));
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.MOOD_CHECK);
    }

    @Test
    void summaryOnlyFailure_doesNotBreakTheConversation() {
        store.saveFlow(SID, ChatFlow.MOOD_CHECK);
        store.saveSummary(SID, "기존");
        validation(true, "positive", "", ok("main", "validate_mood"));
        when(ml.chat(any())).thenReturn(new MlChatResponse(SID, "취미 뭐야?", "", List.of(), "기존", "x", null, null,
                List.of(ok("main", "m"), fail("summary", "summary_update", "SCHEMA_INVALID"))));

        ChatResponse res = service.chat(CID, SID, "기분 좋아");

        assertThat(res.message()).isEqualTo("취미 뭐야?");
        assertThat(store.getSummary(SID)).isEqualTo("기존");
        assertThat(store.getFlow(SID)).isEqualTo(ChatFlow.HOBBY_CHECK);
        verify(logs).record(eq(SID), eq("hobby_check"), argThat(l -> l.size() == 2));
    }

    // ---------- 기타 ----------
    @Test
    void unknownCustomer_isRejected() {
        when(customers.findCustomerCacheInfoById(99L)).thenReturn(null);

        assertThatThrownBy(() -> service.chat(99L, SID, "hi")).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(ml);
    }

    @Test
    void missingCustomerId_isRejected() {
        assertThatThrownBy(() -> service.chat(null, SID, "hi")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void clearSession_removesAllSessionState() {
        store.saveFlow(SID, ChatFlow.CHOOSE);
        store.saveMood(SID, "positive");
        store.saveSummary(SID, "s");
        store.savePrevKeyword(SID, "k");
        store.saveCount(SID, 2);

        service.clearSession(SID);

        assertThat(store.isEmpty(SID)).isTrue();
    }

    @Test
    void userRawTextIsNotPassedToCallLog() {
        store.saveFlow(SID, ChatFlow.MOOD_CHECK);
        validation(true, "positive", "", ok("main", "validate_mood"));
        chatOk("취미 뭐야?", "s", null, List.of());

        service.chat(CID, SID, "비밀스러운 원문 입력");

        // 로그 서비스는 (대화 id, 단계, 메타데이터 목록)만 받는다. 원문을 받을 인자가 없다.
        ArgumentCaptor<List<MlCallMeta>> c = ArgumentCaptor.forClass(List.class);
        verify(logs, times(2)).record(eq(SID), anyString(), c.capture());
        assertThat(c.getAllValues().toString()).doesNotContain("비밀스러운 원문 입력");
    }
}
