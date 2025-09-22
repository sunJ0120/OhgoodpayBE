package com.ohgoodteam.ohgoodpay.shorts.controller.feed;

import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsCommentRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsPointEarnRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsReactionRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ApiResponseWrapper;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.*;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ShortsCommonResponse;
import com.ohgoodteam.ohgoodpay.shorts.service.feed.ShortsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
            @RequestParam (value = "keyword", required = false) String keyword,
            @RequestParam (value = "customerId", required = false) Long customerId
    ) {
        log.info("전체 쇼츠 피드 조회 요청");

        List<ShortsFeedDataDto> data = shortsFeedService.findAllFeeds(page,size,keyword,customerId );

        ShortsCommonResponse res = ShortsCommonResponse.builder()
                .resultCode("0000")
                .resultMsg("성공")
                .data(data)
                .build();

        return ResponseEntity.ok(res);
    }


    @GetMapping("/feeds-v2")
    public ApiResponseWrapper<ShortsFeedListDataDto> getAllFeedV2(
            @RequestParam (value = "page") int page,
            @RequestParam (value = "size", defaultValue = "10", required = false) int size,
            @RequestParam (value = "keyword", required = false) String keyword
    ){
        log.info("전체 쇼츠 피드 조회 요청 v2 : page={}, size={}, keyword={}", page, size, keyword);

        ShortsFeedListDataDto data  = shortsFeedService.findAllFeedsV2(page,size,keyword);

        return ApiResponseWrapper.ok(data);
    }

    /**
     * 커서 기반 페이징을 사용한 전체 쇼츠 피드 조회 (가중치 적용)
     * @param limit 조회할 개수
     * @param lastScore 마지막 점수 (커서)
     * @param lastDate 마지막 날짜 (커서)
     * @param lastId 마지막 ID (커서)
     * @param customerId 고객 ID
     * @return 커서 기반 페이징 결과
     */
    @GetMapping("/feeds-cursor")
    public ApiResponseWrapper<ShortsFeedCursorResponseDto> getAllFeedWithCursor(
            @RequestParam (value = "limit", defaultValue = "24", required = false) Integer limit,
            @RequestParam (value = "lastScore", required = false) Double lastScore,
            @RequestParam (value = "lastDate", required = false) LocalDateTime lastDate,
            @RequestParam (value = "lastId", required = false) Long lastId,
            @RequestParam (value = "customerId", required = false) Long customerId
    ){
        log.info("커서 기반 쇼츠 피드 조회 요청 : limit={}, lastScore={}, lastDate={}, lastId={}, customerId={}", 
                limit, lastScore, lastDate, lastId, customerId);

        ShortsFeedCursorResponseDto data = shortsFeedService.findAllFeedsWithCursor(
                limit, lastScore, lastDate, lastId, customerId);

        return ApiResponseWrapper.ok(data);
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

    @GetMapping("/feeds-v2/{shortsId}/comments")
    public ApiResponseWrapper<ShortsCommentListDataDto> getAllCommentsV2(@PathVariable (value = "shortsId") Long shortsId){
        log.info("특정 쇼츠 댓글 조회 요청 : shortsId={}" , shortsId);
        ShortsCommentListDataDto data = shortsFeedService.findAllCommentsV2(shortsId);
        return ApiResponseWrapper.ok(data);
    }

    /**
     * 쇼츠 댓글 작성
     * @param requestDto 댓글 요청 DTO
     * @param shortsId 쇼츠 아이디
     * @return
     */
    @PostMapping("/feeds/{shortsId}/comments")
    public ResponseEntity<Boolean> createComment(
            @RequestBody ShortsCommentRequestDto requestDto,
            @PathVariable (value = "shortsId") Long shortsId
    ){
        log.info("쇼츠 댓글 작성 요청: shortsId={}, requestDto={}", shortsId, requestDto);
        boolean data = shortsFeedService.createComment(shortsId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(data);

    }


    @PostMapping("/point/earn")
    public ResponseEntity<ShortsPointEarnResponseDto> earnPoint(
        @RequestBody ShortsPointEarnRequestDto requestDto
    ) {
        log.info("포인트 적립 요청 : requestDto={}", requestDto);
        ShortsPointEarnResponseDto response = shortsFeedService.earnPoint(requestDto);
        return ResponseEntity.ok(response);
    }


    /* 
    // 5초마다 호출해서 포인트 지급 가능한지 체크
    @GetMapping("/pointstatus")
    public ResponseEntity<ShortsPointResponseDto> getPointStatus(
        @RequestParam("customerId") Long customerId
    ){
        return ResponseEntity.ok(shortsFeedService.getPointStatus(customerId));
    }

    // 시청시간 계산 후 포인트 적립
    @PostMapping("/watch/feed")
    public ResponseEntity<ShortsPointResponseDto> watchFeed(
        @RequestParam("customerId") Long customerId,
        @RequestBody ShortsPointRequestDto requestDto
    ){
        return ResponseEntity.ok(shortsFeedService.watchFeed(customerId, requestDto));
    }
        */


    @PostMapping("/feeds-v2/{shortsId}/comments")
    public ApiResponseWrapper<ShortsCommentDataDto> createCommentV2(
            @PathVariable (value = "shortsId") Long shortsId,
            @RequestBody ShortsCommentRequestDto requestDto
        ){
        log.info("특정 쇼츠 댓글 작성 요청 : shortsId={}, requestDto={}", shortsId, requestDto);
        ShortsCommentDataDto dto = shortsFeedService.createCommentV2(shortsId, requestDto);

        return ApiResponseWrapper.ok(dto);
    }

    @PostMapping("/feeds/{shortsId}/reactions")
    public ApiResponseWrapper<ShortsReactionDataDto> reactToShorts(
            @PathVariable (value ="shortsId") Long shortsId,
            @RequestBody ShortsReactionRequestDto requestDto
    ){
        log.info("쇼츠 반응(좋아요/싫어요) 요청: shortsId={}, requestDto={}", shortsId, requestDto);
        try {
            ShortsReactionDataDto dto = shortsFeedService.reactToShorts(requestDto);
            return ApiResponseWrapper.ok(dto);
        } catch (IllegalArgumentException e){
            // 이미 반응한 쇼츠
            return ApiResponseWrapper.error(400, e.getMessage());
        }

    }


    /**
     * 댓글 삭제
     */
    @DeleteMapping("/feeds/{shortsId}/comments/{commentId}/delete")
    public ApiResponseWrapper<ShortsCommentDeleteDataDto> deleteComment(
            @PathVariable (value="shortsId") Long shortsId,
            @PathVariable (value ="commentId" ) Long commentId,
            @RequestParam (value="customerId") Long customerId
    ){
        log.info("댓글 삭제 요청: shortsId={}, commentId={}, customerId={}", shortsId, commentId, customerId);
        try {
            ShortsCommentDeleteDataDto dto = shortsFeedService.deleteComment(shortsId, commentId, customerId);
            return ApiResponseWrapper.ok(dto);
        } catch (IllegalArgumentException e){
            return ApiResponseWrapper.error(400, e.getMessage());
        }
    }



    @GetMapping("/{shortsId}")
    public ResponseEntity<ShortsFeedDataDto> getSpecificShorts(
        @PathVariable (value = "shortsId") Long shortsId
    ){
        return ResponseEntity.ok(shortsFeedService.getSpecificShorts(shortsId));
    }


}
