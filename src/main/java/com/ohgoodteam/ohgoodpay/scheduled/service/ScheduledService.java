package com.ohgoodteam.ohgoodpay.scheduled.service;

public interface ScheduledService {
    
    // 매월 1일 등급 업데이트, 등급으로 한도 초기화
    void updateCustomerGrade();

    // 매월 1일 제재 적용
    void applyPenalty();

    // 매월 16일 자동 연장 업데이트
    void updateCustomerAutoExtension();

    // 매월 15일 납부 요청
    // void requestPaymentAt15();

    // 매월 말일 납부 요청
    // void requestPaymentAtLastDay();
}
