package com.ohgoodteam.ohgoodpay.shorts.dto.response.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortsUploadResponseDto {
    private boolean success;
    private String message;
}
