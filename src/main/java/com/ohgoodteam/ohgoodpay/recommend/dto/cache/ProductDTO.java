package com.ohgoodteam.ohgoodpay.recommend.dto.cache;

import lombok.*;

/**
 * FAST API - product에 들어가는 DTO
 */
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private int rank;
    private String name;
    private int price;
    private String image;
    private String url;
    private String category;
}
