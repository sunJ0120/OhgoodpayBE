package com.ohgoodteam.ohgoodpay.recommend.dto.datadto.recommenddto;

import com.ohgoodteam.ohgoodpay.recommend.dto.cache.CustomerCacheDTO;
import lombok.*;

/*
사용자 정보와 요청을 담은 dto
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeywordGenerateRequestDTO {
    private CustomerCacheDTO customerInfo;
    private String mood;
    private String hobby;
    private String category;
    private Integer balance;

    public static KeywordGenerateRequestDTO of(CustomerCacheDTO cacheDto, String mood, String hobby, String category, Integer balance) {
        return KeywordGenerateRequestDTO.builder()
                .customerInfo(cacheDto)
                .mood(mood)
                .hobby(hobby)
                .category(category)
                .balance(balance)
                .build();
    }
}
