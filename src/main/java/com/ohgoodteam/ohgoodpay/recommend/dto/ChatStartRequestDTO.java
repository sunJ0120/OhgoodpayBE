package com.ohgoodteam.ohgoodpay.recommend.dto;

import lombok.*;

/**
 * 채팅 시작 요청 DTO
 * 
 * 프론트엔드에서 채팅 시작 시 전달하는 최소한의 정보
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatStartRequestDTO {
    private Long customerId;
}