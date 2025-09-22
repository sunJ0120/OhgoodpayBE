package com.ohgoodteam.ohgoodpay.shorts.service.feed;

import com.ohgoodteam.ohgoodpay.common.entity.CommentEntity;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.ReactionEntity;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
// import com.ohgoodteam.ohgoodpay.shorts.Converter;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsCommentRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsPointEarnRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsCommentDataDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsFeedDataDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsReactionRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.*;
import com.ohgoodteam.ohgoodpay.shorts.repository.CommentRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.ReactionRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.ShortsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortsFeedServiceImpl implements ShortsFeedService {

    private final CustomerRepository customerRepository;
    private final ShortsRepository shortsRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;

    @Value("${ranking.w.like:1.5}")    private double wLike;
    @Value("${ranking.w.comment:1.2}") private double wComment;
    @Value("${ranking.w.hashtag:1.2}") private double wHashtag;
    @Value("${ranking.w.recency:1.5}") private double wRecency;
    @Value("${ranking.tau.hours:72}")  private double tauHours;


    private static final int POINTS_PER_60_SECONDS = 10;
    private static final int DAILY_POINT_LIMIT = 100;
    private static final String POINT_REASON = "숏폼 시청";

    // private final Converter converter;

    /**
     * 전체 쇼츠 피드 조회 (기존 Pageable 방식)
     * @param page
     * @param size
     * @param keyword
     * @return
     */
    @Override
    public List<ShortsFeedDataDto> findAllFeeds(int page, int size, String keyword, Long customerId) {
        log.info("findAllFeeds 호출 : page={}, size={}, keyword={}, customerId={}", page, size, keyword, customerId);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Object[]> results = shortsRepository.findAllFeeds(wLike, wComment, wHashtag, wRecency, tauHours, customerId, pageable);

        // Object[] 배열을 ShortsFeedDataDto로 변환
        List<ShortsFeedDataDto> result = new ArrayList<>();
        for (Object[] row : results.getContent()) {
            result.add(convertToShortsFeedDataDto(row));
        }

        log.info("JPA 조회 결과: {}", result);
        log.info("페이징 정보 - 현재페이지: {}, 전체페이지: {}, 다음페이지존재: {}", 
                results.getNumber() + 1, results.getTotalPages(), results.hasNext());

        return result;
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
    public ShortsFeedCursorResponseDto findAllFeedsWithCursor(Integer limit, Double lastScore, 
                                                             LocalDateTime lastDate, Long lastId, Long customerId) {
        log.info("findAllFeedsWithCursor 호출 : limit={}, lastScore={}, lastDate={}, lastId={}, customerId={}", 
                limit, lastScore, lastDate, lastId, customerId);

        int pageSize = (limit == null ? 24 : Math.min(limit, 50));
        
        List<Object[]> rows = shortsRepository.findAllFeedsWithCursor(
                wLike, wComment, wHashtag, wRecency, tauHours, 
                customerId, lastScore, lastDate, lastId, pageSize + 1);

        boolean hasNext = rows.size() > pageSize;
        if (hasNext) {
            rows = rows.subList(0, pageSize);
        }

        // Object[] 배열을 ShortsFeedDataDto로 변환
        List<ShortsFeedDataDto> feeds = new ArrayList<>();
        for (Object[] row : rows) {
            feeds.add(convertToShortsFeedDataDtoWithScore(row));
        }

        // 다음 커서 정보 생성
        ShortsFeedCursorResponseDto.NextCursor next = null;
        if (hasNext && !rows.isEmpty()) {
            Object[] lastRow = rows.get(rows.size() - 1);
            Double score = ((Number) lastRow[12]).doubleValue(); // score는 12번째 인덱스
            next = new ShortsFeedCursorResponseDto.NextCursor(
                (Long) lastRow[0], // shortsId
                ((Timestamp) lastRow[5]).toLocalDateTime(), // date
                score
            );
        }

        log.info("커서 기반 조회 결과: {}개, hasNext: {}", feeds.size(), hasNext);

        return ShortsFeedCursorResponseDto.builder()
                .feeds(feeds)
                .next(next)
                .hasNext(hasNext)
                .build();
    }

    /**
     * 전체 쇼츠 피드 조회 (페이징 정보 포함)
     */
    public ShortsFeedListDataDto  findAllFeedsWithPaging(int page, int size, String keyword, Long customerId) {
        log.info("findAllFeedsWithPaging 호출 : page={}, size={}, keyword={}, customerId={}", page, size, keyword, customerId);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Object[]> results = shortsRepository.findAllFeeds(wLike, wComment, wHashtag, wRecency, tauHours, customerId, pageable);

        // Object[] 배열을 ShortsFeedDataDto로 변환
        List<ShortsFeedDataDto> shortsFeedList = new ArrayList<>();
        for (Object[] row : results.getContent()) {
            shortsFeedList.add(convertToShortsFeedDataDto(row));
        }

        log.info("JPA 조회 결과: {}", shortsFeedList);
        log.info("페이징 정보 - 현재페이지: {}, 전체페이지: {}, 다음페이지존재: {}", 
                results.getNumber() + 1, results.getTotalPages(), results.hasNext());

        return ShortsFeedListDataDto.builder()
                .shortsFeedList(shortsFeedList)
                .hasNext(results.hasNext())
                .build();
    }

    /**
     * Object[] 배열을 ShortsFeedDataDto로 변환
     */
    private ShortsFeedDataDto convertToShortsFeedDataDto(Object[] row) {
        // 각 컬럼을 의미있는 변수명으로 추출
        Long shortsId = (Long) row[0];
        String videoName = (String) row[1];
        String thumbnail = (String) row[2];
        String shortsName = (String) row[3];
        String shortsExplain = (String) row[4];
        LocalDateTime date = ((Timestamp) row[5]).toLocalDateTime();
        Long customerId = (Long) row[6];
        String customerNickname = (String) row[7];
        String profileImg = (String) row[8];
        int likeCount = ((Number) row[9]).intValue();
        int commentCount = ((Number) row[10]).intValue();
        String myReaction = (String) row[11];
        Double score = ((Number) row[12]).doubleValue(); // score는 12번째 인덱스
        
        // 점수 콘솔 출력
        log.info("쇼츠 ID: {}, 제목: {}, 좋아요: {}, 댓글: {}, 점수: {}", 
                shortsId, shortsName, likeCount, commentCount, String.format("%.6f", score));
        
        return ShortsFeedDataDto.builder()
                .shortsId(shortsId)
                .videoName(videoName)
                .thumbnail(thumbnail)
                .shortsName(shortsName)
                .shortsExplain(shortsExplain)
                .date(date)
                .customerId(customerId)
                .customerNickname(customerNickname)
                .profileImg(profileImg)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .myReaction(myReaction)
                .build();
    }

    /**
     * Object[] 배열을 ShortsFeedDataDto로 변환 (score 포함)
     */
    private ShortsFeedDataDto convertToShortsFeedDataDtoWithScore(Object[] row) {
        // 각 컬럼을 의미있는 변수명으로 추출
        Long shortsId = (Long) row[0];
        String videoName = (String) row[1];
        String thumbnail = (String) row[2];
        String shortsName = (String) row[3];
        String shortsExplain = (String) row[4];
        LocalDateTime date = ((Timestamp) row[5]).toLocalDateTime();
        Long customerId = (Long) row[6];
        String customerNickname = (String) row[7];
        String profileImg = (String) row[8];
        int likeCount = ((Number) row[9]).intValue();
        int commentCount = ((Number) row[10]).intValue();
        String myReaction = (String) row[11];
        Double score = ((Number) row[12]).doubleValue(); // score는 12번째 인덱스
        
        // 점수 콘솔 출력
        log.info("쇼츠 ID: {}, 제목: {}, 좋아요: {}, 댓글: {}, 점수: {}", 
                shortsId, shortsName, likeCount, commentCount, String.format("%.6f", score));
        
        return ShortsFeedDataDto.builder()
                .shortsId(shortsId)
                .videoName(videoName)
                .thumbnail(thumbnail)
                .shortsName(shortsName)
                .shortsExplain(shortsExplain)
                .date(date)
                .customerId(customerId)
                .customerNickname(customerNickname)
                .profileImg(profileImg)
                .likeCount(likeCount)
                .commentCount(commentCount)
                .myReaction(myReaction)
                .build();
    }

    /**
     * 전체 쇼츠 피드 조회 V2
     * @param page
     * @param size
     * @param keyword
     * @return
     */
    @Override
    public ShortsFeedListDataDto findAllFeedsV2(int page, int size, String keyword) {
        // jpa 페이징 조회 (PageRequest 사용)
        Pageable pageable = PageRequest.of(page -1, size);

        // jpa 페이징 조회 (Page 사용)
        Page<ShortsEntity> shortsPage = shortsRepository.findAllFeedsV2(keyword, pageable);

        // 응답 데이터 변환
        List<ShortsFeedDataDto> feeds = new ArrayList<>();
        for( ShortsEntity item : shortsPage ) {
            ShortsFeedDataDto dto = new ShortsFeedDataDto(item);
            feeds.add(dto);
        }

        // 응답 데이터 생성
        return ShortsFeedListDataDto.builder()
                .shortsFeedList(feeds)
                .hasNext(shortsPage.hasNext())
                .build();
    }

    /**
     * 댓글 조회
     * @param shortsId
     * @return
     */
    @Override
    public List<ShortsCommentDataDto> findAllComments(Long shortsId) {

        List<CommentEntity> result = commentRepository.findAllComments(shortsId);

        List<ShortsCommentDataDto> data = new ArrayList<>();
        for(CommentEntity item : result){
            ShortsCommentDataDto dto = new ShortsCommentDataDto(item);
            data.add(dto);
        }
        return data;
    }

    /**
     * 댓글 조회 V2
     * @param shortsId
     * @return
     */
    @Override
    public ShortsCommentListDataDto findAllCommentsV2(Long shortsId) {

        List<CommentEntity> result = commentRepository.findAllComments(shortsId);

        List<ShortsCommentDataDto> comments = new ArrayList<>();

        for(CommentEntity item : result) {
            ShortsCommentDataDto dto = new ShortsCommentDataDto(item);
            comments.add(dto);
        }

        return ShortsCommentListDataDto.builder()
                .comments(comments)
                .build();
    }

    /**
     * 댓글 작성
     * @param shortsCommentRequestDto
     * @return
     */
    @Override
    @Transactional
    public boolean createComment(Long shortsId, ShortsCommentRequestDto shortsCommentRequestDto) {
        // 1. 작성자 조회
        CustomerEntity customer = customerRepository.findById(shortsCommentRequestDto.getCustomerId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 쇼츠 조회
       ShortsEntity shorts =  shortsRepository.findById(shortsId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쇼츠입니다."));

        // 3. 댓글 생성
        CommentEntity comment = CommentEntity.builder()
                .customer(customer)
                .shorts(shorts)
                .gno(shortsCommentRequestDto.getGno())
                .content(shortsCommentRequestDto.getContent())
                .date(LocalDateTime.now())
                .build();

        // 4. DB 저장
         commentRepository.save(comment);

        // 5. 댓글 작성 시 쇼츠의 댓글 수 증가

        int result = shortsRepository.incrementCommentCount(shortsId);


        // 6. 성공 여부 반환
        return result > 0;
    }

    /**
     * 댓글 작성 V2
     * @param shortsId
     * @param shortsCommentRequestDto
     * @return
     */
    @Override
    @Transactional
    public ShortsCommentDataDto createCommentV2(Long shortsId, ShortsCommentRequestDto shortsCommentRequestDto) {

        CustomerEntity customer  = customerRepository.findById(shortsCommentRequestDto.getCustomerId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        ShortsEntity shorts = shortsRepository.findById(shortsId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쇼츠입니다."));

        CommentEntity comment = CommentEntity.builder()
                .customer(customer)
                .shorts(shorts)
                .gno(shortsCommentRequestDto.getGno())
                .content(shortsCommentRequestDto.getContent())
                .date(LocalDateTime.now())
                .build();

        CommentEntity saved =  commentRepository.save(comment);

        shortsRepository.incrementCommentCount(shortsId);

        return new ShortsCommentDataDto(saved);
    }

    /* 
    @Override
    @Transactional(readOnly = true)
    public ShortsPointResponseDto getPointStatus(Long customerId) {
        LocalDateTime start = startOfToday();
        LocalDateTime end = startOfTomorrow();

        int todaySum = shortsRepository.sumTodayShortsPoint(customerId, REASON, start, end);
        String k = key(customerId, todayKST());
        int progress = carrySecMap.getOrDefault(k, 0);

        int lapsDone = todaySum / POINT_PER_LAP;
        int todayAccumSec = lapsDone * LAP_SECONDS + progress;

        return new ShortsPointResponseDto(
            todayAccumSec, // 오늘 총 시청시간 보여줄려면
            todaySum, // 오늘 요청시까지 지급된 포인트
            progress, // 게이지 바 그리는 용도
            DAILY_CAP, // 하루 포인트 최대치 채우면 더 이상 포인트 적립 안됨
            POINT_PER_LAP, // 한 사이클 당 포인트
            false
        );
    }

    @Override
    @Transactional
    public ShortsPointResponseDto watchFeed(Long customerId, ShortsPointRequestDto requestDto) {
        long nowMs = System.currentTimeMillis();
        long prev  = lastBeatMs.getOrDefault(customerId, nowMs);
        long deltaMs = Math.max(0, nowMs - prev);
        lastBeatMs.put(customerId, nowMs);

        long deltaSec = Math.min(MAX_DELTA_SEC, deltaMs / 1000);
        if (!requestDto.isPlaying()) deltaSec = 0;

        // 동시성 제어 -> 고객 테이블 잠금
        shortsRepository.lockCustomerRow(customerId);

        // 오늘의 합계/상한 체크
        LocalDateTime start = startOfToday();
        LocalDateTime end   = startOfTomorrow();

        int todaySum = shortsRepository.sumTodayShortsPoint(customerId, REASON, start, end); // 이미 적립된 오늘 포인트
        int remainingForToday = Math.max(0, DAILY_CAP - todaySum);

        // 캐리초 불러오기
        String k = key(customerId, todayKST());
        int carry = carrySecMap.getOrDefault(k, 0);

        // 누적
        int add = (int) deltaSec;
        int totalSec = carry + add;

        boolean rewardedNow = false;

        while (totalSec >= LAP_SECONDS && remainingForToday >= POINT_PER_LAP) {
            // 포인트 적립(10p)
            shortsRepository.insertPointHistory(customerId, POINT_PER_LAP, REASON, kstNow());
            shortsRepository.addCustomerPoint(customerId, POINT_PER_LAP);

            remainingForToday -= POINT_PER_LAP;
            totalSec -= LAP_SECONDS;
            todaySum += POINT_PER_LAP;
            rewardedNow = true;
        }

        // 캐리초 저장(0~59)
        int progress = Math.min(totalSec, LAP_SECONDS - 1);
        carrySecMap.put(k, progress);

        // 오늘 누적 시청초 
        int lapsDone = todaySum / POINT_PER_LAP;
        int todayAccumSec = lapsDone * LAP_SECONDS + progress;
        
        return new ShortsPointResponseDto(
            todayAccumSec,
            todaySum,
            progress,
            DAILY_CAP,
            POINT_PER_LAP,
            rewardedNow
        );
    }
        */
    
    /**
     * 좋아요/싫어요 반응처리
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public ShortsReactionDataDto reactToShorts(ShortsReactionRequestDto dto) {
        // 1. 작성자 조회
        CustomerEntity customer = customerRepository.findById(dto.getCustomerId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 쇼츠 조회
        ShortsEntity shorts = shortsRepository.findById(dto.getShortsId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쇼츠입니다."));

        // 3. 요청 타입 (대소문자 무시 -> 항상 대문자로 저장)
//        String requestedType = dto.getType().toUpperCase();
        String requestedType = dto.getType();

        // 4. 기존 반응 조회
        Optional<ReactionEntity> existingReaction = reactionRepository.findByCustomerAndShorts(customer, shorts);

        // 5. 기존 반응이 존재하는 경우
        if(existingReaction.isPresent()){
            String currentType = existingReaction.get().getReact();

            if(currentType.equals(requestedType)){
                // 같은 반응 -> 취소 (토글)
                reactionRepository.delete(existingReaction.get());

                if("like".equals(requestedType)){
                    // 좋아요 취소 -> 좋아요 수 감소
                    shortsRepository.decrementLikeCount(dto.getShortsId());
                }
                // 싫어요 취소는 좋아요 수 변경 없음

                // 최종 좋아요 수 조회
//                ShortsEntity updatedShorts = shortsRepository.findById(dto.getShortsId()).orElseThrow();
//                int likeCount = (int) updatedShorts.getLikeCount();
                int likeCount = shortsRepository.findLikeCountById(dto.getShortsId());

                return ShortsReactionDataDto.builder()
                        .shortsId(dto.getShortsId())
                        .likeCount(likeCount)
                        .myReaction(null)
                        .build();
            }
            else {
                // 다른 반응으로 전환
                reactionRepository.updateReaction(customer, shorts, requestedType);

                if("like".equals(currentType) && "dislike".equals(requestedType)){
                    // 좋아요 -> 싫어요: 좋아요 취소되므로 좋아요 수 감소
                    shortsRepository.decrementLikeCount(dto.getShortsId());
                } else if("dislike".equals(currentType) && "like".equals(requestedType)){
                    // 싫어요 -> 좋아요: 좋아요 저장되므로 좋아요 수 증가
                    shortsRepository.incrementLikeCount(dto.getShortsId());
                }

                int likeCount = shortsRepository.findLikeCountById(dto.getShortsId());

                return ShortsReactionDataDto.builder()
                        .shortsId(dto.getShortsId())
                        .likeCount(likeCount)
                        .myReaction(requestedType)
                        .build();

            }
        }

        // 6. 새 반응 저장 (기존 반응이 없거나 다른 반응으로 전환하는 경우)

        ReactionEntity reaction = ReactionEntity.builder()
                .react(requestedType)
                .customer(customer)
                .shorts(shorts)
                .build();

        reactionRepository.save(reaction);

        if("like".equals(requestedType)){
            // 좋아요 저장 -> 좋아요 수 증가
            shortsRepository.incrementLikeCount(dto.getShortsId());
        }
        // 싫어요 저장은 좋아요 수 변경 없음

        // 7. 최종 좋아요 수 조회
//        ShortsEntity updatedShorts = shortsRepository.findById(dto.getShortsId()).orElseThrow();
//        int likeCount = (int) updatedShorts.getLikeCount();
        int likeCount = shortsRepository.findLikeCountById(dto.getShortsId());

        return ShortsReactionDataDto.builder()
                .shortsId(dto.getShortsId())
                .likeCount(likeCount)
                .myReaction(requestedType)
                .build();
    }


    /**
     * 댓글 삭제
     * @param shortsId
     * @param commentId
     * @param customerId
     * @return
     */
    @Override
    public ShortsCommentDeleteDataDto deleteComment(Long shortsId, Long commentId, Long customerId) {

        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!comment.getShorts().getShortsId().equals(shortsId)) {
            throw new IllegalArgumentException("해당 피드에 속한 댓글이 아닙니다.");
        }

        if (!comment.getCustomer().getCustomerId().equals(customerId)) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.deleteById(commentId);

        return ShortsCommentDeleteDataDto.builder()
                .commentId(commentId)
                .shortsId(shortsId)
                .customerId(customerId)
                .deleted(true)
                .build();
    }

    // 숏폼 피드 공유 기능 -> 특정 영상에 대한 정보 반환
    @Override
    public ShortsFeedDataDto getSpecificShorts(Long shortsId) {
        ShortsEntity shorts = shortsRepository.findById(shortsId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쇼츠입니다."));
        return new ShortsFeedDataDto(shorts);
    }

    // 포인트 게이지 적립
    @Override
    @Transactional
    public ShortsPointEarnResponseDto earnPoint(ShortsPointEarnRequestDto requestDto) {
        Long customerId = requestDto.customerId();
        int watchedSeconds = requestDto.watchedSeconds();
        
        // 1. 오늘 적립된 포인트 합계 조회
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime startOfTomorrow = startOfToday.plusDays(1);
        
        int todayTotalPoints = shortsRepository.sumTodayPoints(
            customerId, POINT_REASON, startOfToday, startOfTomorrow);
        
        // 2. 적립 가능한 포인트 계산
        int calculatedPoints = (int) Math.round((double) watchedSeconds * POINTS_PER_60_SECONDS / 60);
        int remainingLimit = DAILY_POINT_LIMIT - todayTotalPoints;
        int earnedPoints = Math.min(calculatedPoints, remainingLimit);
        log.info("calculatedPoints", calculatedPoints);
        log.info("remainingLimit", remainingLimit);
        log.info("earnedPoints", earnedPoints);
        
        // 3. 포인트 적립 
        if (earnedPoints > 0) {
            // 포인트 히스토리 저장
            shortsRepository.insertPointHistory(
                customerId, earnedPoints, POINT_REASON, LocalDateTime.now());
            
            // 사용자 포인트 업데이트
            shortsRepository.updateCustomerPoints(customerId, earnedPoints);
            
            return new ShortsPointEarnResponseDto(
                earnedPoints,
                todayTotalPoints + earnedPoints,
                true,
                earnedPoints + "포인트 적립 완료!"
            );
        } else {
            return new ShortsPointEarnResponseDto(
                0,
                todayTotalPoints,
                false,
                remainingLimit == 0 ? "일일 포인트 한도 초과" : "적립 가능한 포인트 없음"
            );
        }
    }
}



