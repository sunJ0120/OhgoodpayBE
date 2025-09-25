package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import lombok.*;

/*
* FAST API - 현재 사용자의 input이 유효한지 검증하는 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidInputRequestDTO {
    private Long customerId;
    private String sessionId; // 프론트에서 관리하는 세션 ID
    private String inputMessage; // 사용자가 보낸 메시지
    private String flow; // 메세지 비교를 위한 대화 흐름, 차후 REDIS에서 가져옴
}