package com.ohgoodteam.ohgoodpay.recommend.dto.dashdto;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSummaryDTO {
    private Long customerId;
    private String username;
    private String grade;
    private Integer score;
}