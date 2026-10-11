package com.ohgoodteam.ohgoodpay.chat.dto;

import com.ohgoodteam.ohgoodpay.llm.dto.MlProduct;

public record ProductDto(
        String name,
        int lprice,
        String image,
        String url,
        String category
) {
    public static ProductDto of(MlProduct product) {
        return new ProductDto(
                product.name(),
                product.price(),
                product.image(),
                product.url(),
                product.category()
        );
    }
}
