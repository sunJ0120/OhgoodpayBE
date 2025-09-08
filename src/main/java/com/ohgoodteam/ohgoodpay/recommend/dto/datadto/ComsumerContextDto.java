package com.ohgoodteam.ohgoodpay.recommend.dto.datadto;

import lombok.*;

/*
소비자 context 정보 dto인데, 이거 초반에 캐싱에 쓰는거라 한번 코드 싹 보고 리팩터링 해야함.
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComsumerContextDto {
    private String mood;
    private String hobby;
//    private Boolean isFirstRecommendation;
}