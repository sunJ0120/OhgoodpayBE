package com.ohgoodteam.ohgoodpay.recommend.dto.basedto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * 기본적으로 fast api의 llm 교환 시 Request에 항상 들어가는 값을 저장한 Base DTO
 * 모든 fast api에서 사용하는 Request DTO는 이 클래스를 상속받아 사용
 */
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
public abstract class BaseLlmRequest{
    private Long customerId;
    private String name;
}
