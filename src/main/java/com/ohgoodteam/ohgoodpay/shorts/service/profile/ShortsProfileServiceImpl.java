package com.ohgoodteam.ohgoodpay.shorts.service.profile;

import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.common.entity.SubscriptionEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.profile.ShortsProfileDataDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.profile.ShortsProfileListDataDto;
import com.ohgoodteam.ohgoodpay.shorts.repository.ShortsRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShortsProfileServiceImpl implements ShortsProfileService {

    private final CustomerRepository customerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ShortsRepository shortsRepository;


    @Override
    public ShortsProfileDataDto getProfile(Long customerId, int page, String sortType) {
        // 1. 사용자 정보 조회
        Optional<CustomerEntity> customer = customerRepository.findById(customerId);
        // 2. 구독 상태 조회
        Long subscriberCount = subscriptionRepository.countFollowings(customerId);
        // 3. 쇼츠 영상 수
        Long videoCount = shortsRepository.countShorts(customerId);
        // 4. 쇼츠 영상 리스트 조회 (페이징)
        Sort sort = Sort.by(Sort.Direction.DESC, "date");

        switch (sortType){
            case "latest":
                sort = Sort.by(Sort.Direction.DESC, "date");
                break;
            case "like":
                sort = Sort.by(Sort.Direction.DESC, "likeCount");
                break;
            case "oldest":
                sort = Sort.by(Sort.Direction.ASC, "date");
                break;
        }

        Pageable pageable = PageRequest.of(page,10,sort);
        Page<Object[]> shortsPage = shortsRepository.findShortsProfileDataByCustomerId(customerId, pageable);
        // 5. DTO 조립 및 반환
        List<ShortsProfileListDataDto> shortsList = new ArrayList<>();
        for (Object[] row : shortsPage.getContent()) {  // ✅ getContent() 추가
            ShortsProfileListDataDto dto = new ShortsProfileListDataDto(
                    (Long) row[0],        // ✅ shortsId
                    (Long) row[1],     // ✅ likeCount (Integer)
                    (String) row[2]       // ✅ thumbnail
            );
            shortsList.add(dto);
        }

        return ShortsProfileDataDto.builder()
                .customerId(customerId)
                .customerNickname(customer.get().getNickname())
                .introduce(customer.get().getIntroduce())
                .profileImg(customer.get().getProfileImg())
                .subscriberCount(subscriberCount)
                .videoCount(videoCount)
                .shortsList(shortsList)
                .hasNext(shortsPage.hasNext())
                .build();
    }

    /**
     * 구독 요청
     * @param customerId
     * @param targetId
     * @return
     */
    @Override
    public long subscribe(Long customerId, Long targetId) {
        // 0. 중복 구독 체크
        boolean alreadySubscribed = subscriptionRepository.existsByFollowerCustomerIdAndFollowingCustomerId(customerId, targetId);
        if(alreadySubscribed){
            throw new IllegalArgumentException("이미 구독한 사용자입니다.");
        }

        // 1. 구독자 정보 조회
        CustomerEntity follower = customerRepository.findById(customerId).orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 구독 대상 정보 조회
        CustomerEntity following = customerRepository.findById(targetId).orElseThrow(()->new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 3. SubscriptionEntity 생성
        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .follower(follower)
                .following(following)
                .build();

        SubscriptionEntity subscriptionEntity = subscriptionRepository.save(subscription);

        return subscriptionEntity.getSubscriptionId();
    }
}
