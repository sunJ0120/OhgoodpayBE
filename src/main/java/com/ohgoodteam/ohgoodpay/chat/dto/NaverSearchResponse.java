package com.ohgoodteam.ohgoodpay.chat.dto;

import java.util.List;

public record NaverSearchResponse (
        String lastBuildDate,
        int total,
        int start,
        int display,
        List<NaverProduct> items
) {
}
