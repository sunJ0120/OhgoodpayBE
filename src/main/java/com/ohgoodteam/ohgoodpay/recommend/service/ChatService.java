package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.recommend.dto.*;

/**
 * 채팅 껍데기 서버 서비스 인터페이스
 *
 * interface 분리로 추후 구현체 변경 용이
 */
public interface ChatService {
    // 채팅 시작
    ChatMessageResponse startChat(Long customerId);
    // 기분 입력
    ChatMessageResponse moodChat(Long customerId, String mood);
    // 취미 확인
    ChatCheckHobbyResponse checkHobby(Long customerId);
    // 취미 업데이트
    ChatUpdateHobbyResponse updateHobby(Long customerId, String newHobby);
    // 구매 이력 (카테고리) 가져오기
    ChatAnalyzePurchasesResponse analyzePurchases(Long customerId);
    // 개인화 상품 추천
    ChatRecommendResponse recommend(Long customerId);
}
