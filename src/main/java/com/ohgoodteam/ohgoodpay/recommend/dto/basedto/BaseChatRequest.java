package com.ohgoodteam.ohgoodpay.recommend.dto.basedto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/*
기본적으로 request에 항상 들어가는 정보를 담은 dto
 */
@Getter
@ToString
@SuperBuilder
@NoArgsConstructor
public abstract class BaseChatRequest {
    private Long customerId;
}
