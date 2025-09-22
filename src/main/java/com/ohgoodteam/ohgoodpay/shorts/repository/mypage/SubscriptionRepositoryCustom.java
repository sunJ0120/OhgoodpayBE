package com.ohgoodteam.ohgoodpay.shorts.repository.mypage;

import java.util.List;
import com.ohgoodteam.ohgoodpay.shorts.repository.mypage.SubscriptionRepository.FollowingRow;

public interface SubscriptionRepositoryCustom {
    
    // 구독 목록 전체보기
    List<FollowingRow> findFollowingPage(Long meId, Long lastSubscriptionId, int size);
}
