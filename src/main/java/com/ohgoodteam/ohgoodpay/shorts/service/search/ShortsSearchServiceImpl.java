package com.ohgoodteam.ohgoodpay.shorts.service.search;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ohgoodteam.ohgoodpay.shorts.repository.ShortsSearchRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDto.CursorResponse;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDto.LayoutItem;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDto.ShortsSearchResponse;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.search.ShortsSearchResponseDto.CursorResponse.NextCursor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortsSearchServiceImpl implements ShortsSearchService {

    private final ShortsSearchRepository shortsSearchRepository;

    // application.properties에 해당 값들이 없으면 기본값으로 설정
    @Value("${ranking.w.like:1.5}")    private double wLike; 
    @Value("${ranking.w.comment:1.2}") private double wComment;
    @Value("${ranking.w.hashtag:1.2}") private double wHashtag;
    @Value("${ranking.w.recency:1.5}") private double wRecency;
    @Value("${ranking.tau.hours:72}")  private double tauHours;

    // 검색 피드 조회
    @Override
    public CursorResponse getFeed(
        String q,
        Integer limit,
        Long lastId,
        LocalDateTime lastDate,
        BigDecimal lastScore
    ){
        int pageSize = (limit == null ? 24 : Math.min(limit, 50));
        List<ShortsSearchResponse> rows = shortsSearchRepository.fetchExposure(
                (q == null || q.isBlank()) ? null : q,
                wLike, wComment, wHashtag, wRecency, tauHours,
                lastScore, lastDate, lastId,
                pageSize + 1
        );
        boolean hasNext = rows.size() > pageSize;
        if (hasNext) {
            rows = rows.subList(0, pageSize);
        }
        List<LayoutItem> items = rows.stream()
                .map(r -> new LayoutItem(r.getShortsId(), r.getThumbnail(), r.getLikeCount()))
                .toList();
        NextCursor next = null;
        if (hasNext && !rows.isEmpty()) {
            var last = rows.get(rows.size() - 1);
            next = new NextCursor(last.getShortsId(), last.getDate(), last.getScore());
        }
        return new CursorResponse(items, next, hasNext);
    }
}
