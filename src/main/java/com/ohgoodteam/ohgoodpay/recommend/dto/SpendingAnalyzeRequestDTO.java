package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.basedto.BaseChatRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;

@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Getter
public class SpendingAnalyzeRequestDTO extends BaseChatRequestDTO {
    private Long customerId;
    // 필요하면 옵션으로 windowMonths 허용 (기본 3)
     private Integer windowMonths;
}