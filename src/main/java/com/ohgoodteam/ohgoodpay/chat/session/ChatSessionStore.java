package com.ohgoodteam.ohgoodpay.chat.session;

import com.ohgoodteam.ohgoodpay.chat.flow.ChatFlow;

/** 대화 세션 단위 상태(흐름 단계, 재시도 횟수, 기분, 요약, 직전 키워드). 운영 구현은 Redis. */
public interface ChatSessionStore {

    /** 저장된 값이 없으면 START */
    ChatFlow getFlow(String sessionId);

    void saveFlow(String sessionId, ChatFlow flow);

    /** 현재 단계에서 검증을 시도한 횟수. 없으면 1 */
    int getCount(String sessionId);

    void saveCount(String sessionId, int count);

    /** 없으면 빈 문자열 */
    String getMood(String sessionId);

    void saveMood(String sessionId, String mood);

    /** 없으면 빈 문자열 */
    String getSummary(String sessionId);

    void saveSummary(String sessionId, String summary);

    /** 직전 추천 키워드. 없으면 null */
    String getPrevKeyword(String sessionId);

    void savePrevKeyword(String sessionId, String keyword);

    void clear(String sessionId);
}
