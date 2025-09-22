package com.ohgoodteam.ohgoodpay.shorts.repository.mypage;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ohgoodteam.ohgoodpay.common.entity.SubscriptionEntity;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long>, SubscriptionRepositoryCustom {

    @Query(value = """
        SELECT COUNT(*)
        FROM subscription s
        WHERE s.follower_id = :meId
    """, nativeQuery = true)
    long countFollowings(@Param("meId") Long meId); // 내가 구독하는 사람 수

    @Query(value = """
        SELECT
          s.subscription_id AS cursorId,
          c.customer_id     AS followingId,
          c.name            AS name,
          c.nickname        AS nickname,
          c.profile_img     AS profileImg
        FROM subscription s
        JOIN customer c ON c.customer_id = s.following_id
        WHERE s.follower_id = :meId
        ORDER BY s.subscription_id DESC
        LIMIT :size
    """, nativeQuery = true) // 구독 미리보기
    List<FollowingRow> findFollowingPreview(@Param("meId") Long meId, @Param("size") int size); // 구독 미리보기

    // 별도의 DTO로 매핑할 수도 있지만 화면,응답에 필요한 필드만 선택적으로 매핑하기 위해서.  DTO 파일을 따로 안만드는게 장점. 
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FollowingRow {
        private Long cursorId;
        private Long followingId;
        private String nickname;
        private String name;
        private String profileImg;
    }

    // 구독 취소
    long deleteByFollowerCustomerIdAndFollowingCustomerId(Long userId, Long targetId);
}
