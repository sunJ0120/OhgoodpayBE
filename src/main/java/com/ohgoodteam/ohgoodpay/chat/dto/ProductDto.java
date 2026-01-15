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
        return new ProductDto(
                product.title().replaceAll("<[^>]*>", ""),  // HTML 태그 제거
                Integer.parseInt(product.lprice()),
                product.image(),
                product.link(),
                product.category1()
        );
    }
}
