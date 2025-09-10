package com.ohgoodteam.ohgoodpay.shorts.service.feed;

import com.ohgoodteam.ohgoodpay.common.entity.CommentEntity;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.shorts.Converter;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.feed.ShortsCommentRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsCommentDataDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.feed.ShortsFeedDataDto;
import com.ohgoodteam.ohgoodpay.shorts.repository.CommentRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.ShortsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortsFeedServiceImpl implements ShortsFeedService {

    private final CustomerRepository customerRepository;
    private final ShortsRepository shortsRepository;
    private final CommentRepository commentRepository;

    private final Converter converter;

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


}
