package com.ohgoodteam.ohgoodpay.pay.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfirmDto {
    private boolean success;
    private boolean result;
}
