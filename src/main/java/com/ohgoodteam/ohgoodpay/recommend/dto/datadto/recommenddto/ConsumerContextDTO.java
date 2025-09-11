package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto;

import lombok.*;

/**
 * FAST API - consumerContext에 들어가는 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerContextDTO {
    private String mood;
    private String hobby;
//    private Boolean isFirstRecommendation;

    public static ConsumerContextDTO of(String mood, String hobby) {
        return ConsumerContextDTO.builder()
                .mood(mood)
                .hobby(hobby)
                .build();
    }
}