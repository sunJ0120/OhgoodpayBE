package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import lombok.*;
import java.util.List;
import com.ohgoodteam.ohgoodpay.recommend.dto.cache.ProductDTO;

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

    private String newHobby;       // 새로 파악한 취미
    private List<ProductDTO> products; // 추천 상품 목록
    private String summary;        // 대화 요약본

    // TODO : 성공하면 이거 정적 팩토리 메서드 생성하기
    // TODO : 시간 남으면 ChatStartResponseDTO 생성해서 응답용으로 감싸기
}
