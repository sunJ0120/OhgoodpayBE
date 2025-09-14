package com.ohgoodteam.ohgoodpay.shorts.service.feed;

import com.ohgoodteam.ohgoodpay.common.entity.CommentEntity;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
// import com.ohgoodteam.ohgoodpay.shorts.Converter;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsCommentRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsPointRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsCommentDataDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsFeedDataDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsPointResponseDto;
import com.ohgoodteam.ohgoodpay.shorts.repository.CommentRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.ShortsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortsFeedServiceImpl implements ShortsFeedService {

    private final CustomerRepository customerRepository;
    private final ShortsRepository shortsRepository;
    private final CommentRepository commentRepository;

    private static final int LAP_SECONDS   = 60;
    private static final int POINT_PER_LAP = 10;
    private static final int DAILY_CAP     = 100;
    private static final long MAX_DELTA_SEC = 5; 

    private static final String REASON = "숏폼";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");


    // 서버 메모리 상태(서버 재시작 시 초기화됨)
    private final Map<Long, Long> lastBeatMs = new ConcurrentHashMap<>();          // 고객별 마지막 수신(ms)
    private final Map<String, Integer> carrySecMap = new ConcurrentHashMap<>();    // key: customerId:yyyyMMdd → 0~59

    private String key(Long customerId, LocalDate day) {
        return customerId + ":" + day.toString();
    }

    private LocalDateTime kstNow() {
        return LocalDateTime.now(KST);
    }

    private LocalDate todayKST() {
        return LocalDate.now(KST);
    }

    private LocalDateTime startOfToday() {
        return todayKST().atStartOfDay();
    }

    private LocalDateTime startOfTomorrow() {
        return startOfToday().plusDays(1);
    }

    // private final Converter converter;

    /**
     * 전체 쇼츠 피드 조회
     * @param page
     * @param size
     * @param keyword
     * @return
     */
    @Override
    public List<ShortsFeedDataDto> findAllFeeds(int page, int size, String keyword) {
        // 페이지
        Pageable pageable = PageRequest.of(page -1 , size);
        List<ShortsEntity> result = shortsRepository.findAllFeeds(keyword, pageable);

//        List<ShortsFeedDataDto> data = result.stream().map(it -> new ShortsFeedDataDto(it)).collect(Collectors.toList());
//        List<ShortsFeedDataDto> data = result.stream().map(it -> ShortsFeedDataDto.entityToDto(it)).collect(Collectors.toList());

//        List<ShortsFeedDataDto> data = result.stream().map(ShortsFeedDataDto::new).collect(Collectors.toList());
//        List<ShortsFeedDataDto> data = result.stream().map(ShortsFeedDataDto::entityToDto).collect(Collectors.toList());

        List<ShortsFeedDataDto> data2 = new ArrayList<>();
        for(ShortsEntity item : result){
            ShortsFeedDataDto dto = new ShortsFeedDataDto(item);
//            ShortsFeedDataDto dto = ShortsFeedDataDto.entityToDto(item);
//            ShortsFeedDataDto dto = converter.entityToDto(item);
            data2.add(dto);
        }

        return data2;
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
     * 댓글 작성
     * @param shortsCommentRequestDto
     * @return
     */
    @Override
    public ShortsCommentDataDto createComment(Long shortsId, ShortsCommentRequestDto shortsCommentRequestDto) {
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
        CommentEntity saved = commentRepository.save(comment);

        // 5. dto 변환 후 반환
        return new ShortsCommentDataDto(saved);
    }

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
}
