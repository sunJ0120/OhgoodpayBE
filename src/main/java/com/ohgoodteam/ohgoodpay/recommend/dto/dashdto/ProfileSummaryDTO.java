package com.ohgoodteam.ohgoodpay.recommend.dto.dashdto;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSummaryDTO {
    private Long customerId;
    private String username;   // nickname 없으면 name
    private String grade;      // grade_name 대문자 정규화
    private Integer score;     // null이면 0 폴백
}