package com.ohgoodteam.ohgoodpay.shorts.controller.feed;

import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsCommentRequestDTO;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsPointEarnRequestDTO;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsReactionRequestDTO;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ApiResponseWrapper;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.*;
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
@RequestMapping("/api")
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
    @GetMapping("/shorts/feeds")
    public ResponseEntity<ShortsCommonResponse> getAllFeed(
            @RequestParam (value = "page") int page,
            @RequestParam (value = "size", defaultValue = "10", required = false) int size,
            @RequestParam (value = "keyword", required = false) String keyword,
            @RequestParam (value = "customerId", required = false) Long customerId
    ) {
        log.info("전체 쇼츠 피드 조회 요청");

        List<ShortsFeedDataDTO> data = shortsFeedService.findAllFeeds(page,size,keyword,customerId );
        System.out.println(data);
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
    @GetMapping("/shorts/feeds/{shortsId}/comments")
    public ResponseEntity<ShortsCommonResponse> getAllComments(@PathVariable (value = "shortsId") Long shortsId){
        log.info("특정 쇼츠 댓글 조회 요청");

        List<ShortsCommentDataDTO> data = shortsFeedService.findAllComments(shortsId);

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
    @PostMapping("/shorts/feeds/{shortsId}/comments")
    public ResponseEntity<Boolean> createComment(
            @RequestBody ShortsCommentRequestDTO requestDto,
            @PathVariable (value = "shortsId") Long shortsId
    ){
        log.info("쇼츠 댓글 작성 요청: shortsId={}, requestDto={}", shortsId, requestDto);
        boolean data = shortsFeedService.createComment(shortsId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(data);
    }

    /**
     * 포인트 적립 요청
     * @param requestDto
     * @return
     */
    @PostMapping("/shorts/point/earn")
    public ResponseEntity<ShortsPointEarnResponseDTO> earnPoint(
        @RequestBody ShortsPointEarnRequestDTO requestDto
    ) {
        log.info("포인트 적립 요청 : requestDto={}", requestDto);
        ShortsPointEarnResponseDTO response = shortsFeedService.earnPoint(requestDto);
        return ResponseEntity.ok(response);
    }


    /**
     * 쇼츠 반응(좋아요/싫어요) 처리
     * @param shortsId
     * @param requestDto
     * @return
     */
    @PostMapping("/shorts/feeds/{shortsId}/reactions")
    public ApiResponseWrapper<ShortsReactionDataDTO> reactToShorts(
            @PathVariable (value ="shortsId") Long shortsId,
            @RequestBody ShortsReactionRequestDTO requestDto
    ){
        log.info("쇼츠 반응(좋아요/싫어요) 요청: shortsId={}, requestDto={}", shortsId, requestDto);
        try {
            ShortsReactionDataDTO dto = shortsFeedService.reactToShorts(requestDto);
            return ApiResponseWrapper.ok(dto);
        } catch (IllegalArgumentException e){
            // 이미 반응한 쇼츠
            return ApiResponseWrapper.error(400, e.getMessage());
        }
    }

    /**
     * 댓글 삭제
     * @param shortsId
     * @param commentId
     * @param customerId
     * @return
     */
    @DeleteMapping("/shorts/feeds/{shortsId}/comments/{commentId}/delete")
    public ApiResponseWrapper<ShortsCommentDeleteDataDTO> deleteComment(
            @PathVariable (value="shortsId") Long shortsId,
            @PathVariable (value ="commentId" ) Long commentId,
            @RequestParam (value="customerId") Long customerId
    ){
        log.info("댓글 삭제 요청: shortsId={}, commentId={}, customerId={}", shortsId, commentId, customerId);
        try {
            ShortsCommentDeleteDataDTO dto = shortsFeedService.deleteComment(shortsId, commentId, customerId);
            return ApiResponseWrapper.ok(dto);
        } catch (IllegalArgumentException e){
            return ApiResponseWrapper.error(400, e.getMessage());
        }
    }

    /**
     * 특정 쇼츠 조회
     * @param shortsId
     * @return
     */
    @GetMapping("/shorts/{shortsId}")
    public ResponseEntity<ShortsFeedDataDTO> getSpecificShorts(
        @PathVariable (value = "shortsId") Long shortsId
    ){
        return ResponseEntity.ok(shortsFeedService.getSpecificShorts(shortsId));
    }
}
