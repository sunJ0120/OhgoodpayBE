package com.ohgoodteam.ohgoodpay.chat.service;

/** LLM 을 쓰지 않는 고정 문장 */
final class ChatMessages {
    private ChatMessages() {}

    static String moodQuestion(String name) {
        return "추천 플로우 시작할게, " + name + "야! 오늘 기분은 어때? (좋아/보통/별로) 😊";
    }
}
