package com.ohgoodteam.ohgoodpay.shorts.dto.request.upload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortsUploadRequestDTO {
    @NotBlank String title;
    @NotBlank String content;
}
