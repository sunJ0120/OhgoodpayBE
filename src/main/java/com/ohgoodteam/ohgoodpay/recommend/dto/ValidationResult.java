package com.ohgoodteam.ohgoodpay.recommend.dto;

import com.ohgoodteam.ohgoodpay.recommend.dto.datadto.llmdto.BasicChatResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Validation 결과를 담는 DTO
 */
@AllArgsConstructor
@Getter
public class ValidationResult {
    private final boolean shouldReturn;
    private final BasicChatResponseDTO response;

    public static ValidationResult continueFlow() {
        return new ValidationResult(false, null);
    }

    public static ValidationResult returnResponse(BasicChatResponseDTO response) {
        return new ValidationResult(true, response);
    }

    public boolean shouldReturn() {
        return shouldReturn;
    }
}