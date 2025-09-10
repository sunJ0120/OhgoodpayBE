package com.ohgoodteam.ohgoodpay.recommend.dto.basedto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 기본적으로 fast api의 llm 교환 시 Response에 항상 들어가는 값을 저장한 Base DTO
 * 모든 fast api에서 사용하는 Response DTO는 이 클래스를 상속받아 사용
 */
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
public class BaseLlmResponse {
    private String message;
}
