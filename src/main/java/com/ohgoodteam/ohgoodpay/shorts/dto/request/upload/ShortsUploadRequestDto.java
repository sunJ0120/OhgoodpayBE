package com.ohgoodteam.ohgoodpay.shorts.dto.request.upload;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShortsUploadRequestDto {
    @NotBlank String title;
    @NotBlank String content;
}
