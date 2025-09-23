package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;
import lombok.*;

/**
 * FAST API - LLM 요청 기본 DTO
 *
 * 챗 요청을 위한 기본 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasicChatRequestDTO {
    private String sessionId; //채팅 redis 저장을 위한 세션 아이디
    private CustomerCacheDTO customerInfo; //채팅 생성 요청을 위한 고객 기본 정보
    private String mood; //채팅 생성 요청을 위한 고객 현재 기분
    private String hobby; //채팅 생성 요청을 위한 고객 취미
    private int balance; //채팅 생성 요청을 위한 고객 현재 잔액
    private String inputMessage; //사용자가 입력한 input message
    private String summary; //이전 대화 요약
    private String flow; //대화 흐름 (예: 추천, 일반대화 등)

    public static BasicChatRequestDTO of(
            String sessionId,
            CustomerCacheDTO customerInfo,
            String mood,
            String hobby,
            int balance,
            String inputMessage,
            String summary,
            String flow
    ) {
        return BasicChatRequestDTO.builder()
                .sessionId(sessionId)
                .customerInfo(customerInfo)
                .mood(mood)
                .hobby(hobby)
                .balance(balance)
                .inputMessage(inputMessage)
                .summary(summary)
                .flow(flow)
                .build();
    }
}
