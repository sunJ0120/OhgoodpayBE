package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import lombok.*;

/**
 * FAST API - LLM 응답 기본 DTO
 *
 * LLM이 주는 챗봇 응답 메세지를 담는 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasicChatResponseDTO{
    private String sessionId;
    private String message;
    
    // TODO : 성공하면 이거 정적 팩토리 메서드 생성하기
    // TODO : 시간 남으면 ChatStartResponseDTO 생성해서 응답용으로 감싸기
}
