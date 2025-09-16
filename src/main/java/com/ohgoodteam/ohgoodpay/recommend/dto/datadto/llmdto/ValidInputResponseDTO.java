package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto;

import lombok.*;

/*
 * FAST API - 현재 사용자의 input이 유효한지 검증 결과를 내보내는 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidInputResponseDTO {
    private Long customerId;
    private String sessionId;
    private String inputMessage; // 사용자가 보낸 메시지, 한 번더 보낼때 재사용 하기 위함이다.
    private boolean isValid; // 입력이 유효한지 여부
    private String message; // LLM이 생성한 응답 메시지, 유효하지 않을 경우에만 사용할 예정.
    private String flow; // 보냈던 대화 흐름 (다음 흐름으로 가도록 하기 위함이다. 이게 있어야 받아서 redis에 플로우 저장하고 다시 llm 요청 가능)
}
