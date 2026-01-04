package com.ohgoodteam.ohgoodpay.pay.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryDTO {
    private Long pointHistoryId;
    private Long customerId;
    private int point;
    private String pointExplain;
    private LocalDateTime date;
}
