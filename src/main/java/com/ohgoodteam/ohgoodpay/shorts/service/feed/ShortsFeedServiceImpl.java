package com.ohgoodteam.ohgoodpay.shorts.service.feed;

import com.ohgoodteam.ohgoodpay.common.entity.CommentEntity;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.ReactionEntity;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
// import com.ohgoodteam.ohgoodpay.shorts.Converter;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsCommentRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsReactionRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.*;
import com.ohgoodteam.ohgoodpay.shorts.repository.CommentRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.ReactionRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.ShortsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortsFeedServiceImpl implements ShortsFeedService {

    private final CustomerRepository customerRepository;
    private final ShortsRepository shortsRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;

    // private final Converter converter;

    /**
     * 전체 쇼츠 피드 조회
     * @param page
     * @param size
     * @param keyword
     * @return
     */
    @Override
    public List<ShortsFeedDataDto> findAllFeeds(int page, int size, String keyword, Long customerId) {
        log.info("findAllFeeds 호출 : page={}, size={}, keyword={}, customerId={}", page, size, keyword, customerId);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Object[]> results = shortsRepository.findAllFeeds(keyword, customerId, pageable);

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
     * 전체 쇼츠 피드 조회 (페이징 정보 포함)
     */
    public ShortsFeedListDataDto findAllFeedsWithPaging(int page, int size, String keyword, Long customerId) {
        log.info("findAllFeedsWithPaging 호출 : page={}, size={}, keyword={}, customerId={}", page, size, keyword, customerId);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Object[]> results = shortsRepository.findAllFeeds(keyword, customerId, pageable);

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
}
