package com.ohgoodteam.ohgoodpay.shorts.controller.feed;

import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsCommentRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsCommentDataDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsFeedDataDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ShortsCommonResponse;
import com.ohgoodteam.ohgoodpay.shorts.service.feed.ShortsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/shorts")
@RequiredArgsConstructor
public class ShortsFeedController {

    private final ShortsFeedService shortsFeedService;

    /**
     * 전체 쇼츠 피드 조회
     * @param page 현재 페이지
     * @param size 페이지당 개수
     * @param keyword 검색 키워드
     * @return
     */
    @GetMapping("/feeds")
    public ResponseEntity<ShortsCommonResponse> getAllFeed(
            @RequestParam (value = "page") int page,
            @RequestParam (value = "size", defaultValue = "10", required = false) int size,
            @RequestParam (value = "keyword", required = false) String keyword
    ) {
        log.info("전체 쇼츠 피드 조회 요청");

        List<ShortsFeedDataDto> data = shortsFeedService.findAllFeeds(page,size,keyword);

        ShortsCommonResponse res = ShortsCommonResponse.builder()
                .resultCode("0000")
                .resultMsg("성공")
                .data(data)
                .build();

        return ResponseEntity.ok(res);
    }

    /**
     * 쇼츠 댓글 조회
     * @param shortsId 쇼츠 아이디
     * @return
     */
    @GetMapping("/feeds/{shortsId}/comments")
    public ResponseEntity<ShortsCommonResponse> getAllComments(@PathVariable (value = "shortsId") Long shortsId){
        log.info("특정 쇼츠 댓글 조회 요청");

        List<ShortsCommentDataDto> data = shortsFeedService.findAllComments(shortsId);

        ShortsCommonResponse res = ShortsCommonResponse.builder()
                .resultCode("0000")
                .resultMsg("성공")
                .data(data)
                .build();

        return ResponseEntity.ok(res);

    }

    /**
     * 쇼츠 댓글 작성
     * @param requestDto 댓글 요청 DTO
     * @param shortsId 쇼츠 아이디
     * @return
     */
    @PostMapping("/feeds/{shortsId}/comments")
    public ResponseEntity createComment(
            @RequestBody ShortsCommentRequestDto requestDto,
            @PathVariable (value = "shortsId") Long shortsId
    ){
        log.info("쇼츠 댓글 작성 요청: shortsId={}, requestDto={}", shortsId, requestDto);
        ShortsCommentDataDto data = shortsFeedService.createComment(shortsId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(data);

    }
}
