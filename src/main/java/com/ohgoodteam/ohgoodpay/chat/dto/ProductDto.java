package com.ohgoodteam.ohgoodpay.chat.dto;

import lombok.*;

public record ProductDto(
        String name,
        int lprice,
        String image,
        String url,
        String category
) {
    public static ProductDto of(NaverProduct product) {
        int price;

        try {
            price = Integer.parseInt(product.lprice());
        } catch (NumberFormatException | NullPointerException e) {
            price = 30000;    // 기본값 설정
        }

        return new ProductDto(
                product.title().replaceAll("<[^>]*>", ""),  // HTML 태그 제거
                price,
                product.image(),
                product.link(),
                product.category1()
        );
    }
}
