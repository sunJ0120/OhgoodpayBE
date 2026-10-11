package com.ohgoodteam.ohgoodpay.chat.flow;

import lombok.Getter;

/**
 * 추천 대화 단계. value 는 ML(FastAPI)과 주고받는 flow 문자열이다.
 *
 * 전이: start → mood_check → hobby_check → choose → recommendation → re-recommendation(반복)
 * (원래 FlowConfiguration 의 전이를 그대로 복원했다.)
 */
@Getter
public enum ChatFlow {
    START("start"),
    MOOD_CHECK("mood_check"),
    HOBBY_CHECK("hobby_check"),
    CHOOSE("choose"),
    RECOMMENDATION("recommendation"),
    RE_RECOMMENDATION("re-recommendation");

    private final String value;

    ChatFlow(String value) {
        this.value = value;
    }

    public ChatFlow next() {
        return switch (this) {
            case START -> MOOD_CHECK;
            case MOOD_CHECK -> HOBBY_CHECK;
            case HOBBY_CHECK -> CHOOSE;
            case CHOOSE -> RECOMMENDATION;
            case RECOMMENDATION, RE_RECOMMENDATION -> RE_RECOMMENDATION;
        };
    }

    /** 이 단계의 사용자 입력을 검증할 프롬프트 키 */
    public String validationPromptKey() {
        return switch (this) {
            case MOOD_CHECK -> "validate_mood";
            case HOBBY_CHECK -> "validate_hobby";
            case CHOOSE -> "validate_choose";
            case RECOMMENDATION -> "validate_recommendation";
            case RE_RECOMMENDATION -> "validate_re_recommendation";
            case START -> "validate_default";
        };
    }

    /** 이 단계의 응답(질문)을 만들 프롬프트 키. mood_check 는 LLM 없이 고정 문장이라 호출하지 않는다. */
    public String chatPromptKey(boolean hasHobby) {
        return switch (this) {
            case HOBBY_CHECK -> hasHobby ? "chat_hobby_check_with_hobby" : "chat_hobby_check_empty";
            case CHOOSE -> "chat_choose";
            case RECOMMENDATION, RE_RECOMMENDATION -> "keyword_extract";
            case START, MOOD_CHECK -> "chat_fallback";
        };
    }

    public static ChatFlow fromValue(String value) {
        for (ChatFlow f : values()) {
            if (f.value.equals(value)) {
                return f;
            }
        }
        return START;
    }
}
