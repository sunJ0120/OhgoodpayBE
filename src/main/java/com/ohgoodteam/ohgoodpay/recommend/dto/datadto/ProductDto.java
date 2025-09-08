package com.ohgoodteam.ohgoodpay.recommend.dto.datadto;

import lombok.*;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private int rank;
    private String name;
    private int price;
    private String image;
    private String url;
    private String category;
}
