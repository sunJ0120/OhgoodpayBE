package com.ohgoodteam.ohgoodpay.pay.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateRequestDto {
    private String codeType;
    private String value;
    private Long customerId;
}
