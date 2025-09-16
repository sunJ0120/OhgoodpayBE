package com.ohgoodteam.ohgoodpay.recommend.dto;

/**
 * 채팅 흐름 타입 Enum
 *
 * 채팅의 흐름을 정의하는 Enum 클래스
 * 흐름 : 인사 & 기분 체크 > 취미 체크 > 뭐가 필요한지 체크 (추천 or 대시보드) > 다시 추천
 */
public enum ChatFlowType {
    MOODCHECK("mood_check"), // 기분 체크하는 단계
    HOBBYCHECK("hobby_check"), // 취미 체크하는 단계
    CHOOSE("choose"), // 추천 or 대시보드 정의하는 단계
    RECOMMENDATION("recommendation"), // 추천하는 단계
    DASHBOARD("dashboard"), // 대시보드 보여주는 단계
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