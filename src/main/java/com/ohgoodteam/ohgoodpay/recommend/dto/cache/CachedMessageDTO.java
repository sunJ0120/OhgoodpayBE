package com.ohgoodteam.ohgoodpay.recommend.dto.cache;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 고객 채팅 정보 DTO
 *
 * Redis에 저장되는 고객 채팅 정보를 담는 객체
 * TTL : 12h
 */
@Getter
@Builder
public class CachedMessageDTO {
    // TODO : 차후 ENUM으로 변경 고려
    private String role; // 역할 (예: user, assistant)
    private String message; // 메시지 내용
    private String timeStamp; // timeStamp를 LocalDateTime > String로 변경
    private int tokens; // 토큰 수
}
