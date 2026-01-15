package com.ohgoodteam.ohgoodpay.chat.service;

import com.ohgoodteam.ohgoodpay.chat.dto.NaverSearchResponse;
import com.ohgoodteam.ohgoodpay.chat.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private static final int MAX_DISPLAY_CNT = 3;
    private static final int MAX_CHECK_CNT = 20;

    private final NaverShoppingClient naverShoppingClient;

    public List<ProductDto> searchAndCache(String keyword, Integer maxPrice) {
        // TODO : 상품 캐싱은 차후 고도화시 적용
        NaverSearchResponse response = naverShoppingClient.search(keyword, MAX_CHECK_CNT);

        return response.items().stream()
                .filter(item -> Integer.parseInt(item.lprice()) <= maxPrice)
                .limit(MAX_DISPLAY_CNT)
                .map(ProductDto::of)
                .toList();
    }
}
