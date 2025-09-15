package com.ohgoodteam.ohgoodpay.recommend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PayThisMonthRequestDTO {
    private Long paymentId;
    private LocalDateTime date;
    private String requestName;
    private Integer price;
    private Integer totalPrice;
    private Integer point;
    private Boolean expired;
}
