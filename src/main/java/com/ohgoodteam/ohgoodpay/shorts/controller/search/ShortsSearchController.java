package com.ohgoodteam.ohgoodpay.shorts.controller.search;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDto.CursorResponse;
import com.ohgoodteam.ohgoodpay.shorts.service.search.ShortsSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShortsSearchController {
    private final ShortsSearchService shortsSearchService;

    // 쇼츠 검색
    @GetMapping("/search")
    public CursorResponse search(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Integer limit,
        @RequestParam(required = false) Long lastId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastDate,
        @RequestParam(required = false) BigDecimal lastScore
    ){
        log.info("🔍 검색 요청 - q: {}, limit: {}, lastId: {}, lastDate: {}, lastScore: {}", 
                q, limit, lastId, lastDate, lastScore);
        
        try {
            CursorResponse result = shortsSearchService.getFeed(q, limit, lastId, lastDate, lastScore);
            log.info("✅ 검색 성공 - 결과 개수: {}, hasNext: {}", 
                    result.items().size(), result.hasNext());
            return result;
        } catch (Exception e) {
            log.error("❌ 검색 실패", e);
            throw e;
        }
    }
}
