package com.ohgoodteam.ohgoodpay.recommend.dto.dashdto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 JSON에서 생략
public class SayMyNameDTO {
    @NotNull private Long customerId;   // 내부 식별자
    @NotNull private String username;   // 닉네임(없으면 이름 폴백)
    @NotNull private String grade;      // DB grade_name → 대문자 정규화
    @NotNull private Integer ohgoodScore; // customer.score (null이면 0 폴백)
    // 필요해지면 사용
    private Integer percentile;         // 선택: 지금은 안 쓰면 null
}
