package com.ohgoodteam.ohgoodpay.recommend.util.flow;

/**
 * 채팅 흐름 타입 Enum
 *
 * 채팅의 흐름을 정의하는 Enum 클래스
 * 흐름 : 인사 & 기분 체크 > 취미 체크 > 추천 > 다시 추천
 */
public enum ChatFlowType {
    START("start"),
    MOODCHECK("mood_check"), // 기분 체크하는 단계
    HOBBYCHECK("hobby_check"), // 취미 체크하는 단계
    CHOOSE("choose"), // 플로우 체크하는 단계
    RECOMMENDATION("recommendation"), // 추천하는 단계
    RE_RECOMMENDATION("re-recommendation"); // 재추천하는 단계

    private final String value;

    ChatFlowType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ChatFlowType fromValue(String value) {
        for (ChatFlowType flowType : ChatFlowType.values()) {
            if (flowType.value.equals(value)) {
                return flowType;
            }
        }
        throw new IllegalArgumentException("Unknown flow type: " + value);
    }
}