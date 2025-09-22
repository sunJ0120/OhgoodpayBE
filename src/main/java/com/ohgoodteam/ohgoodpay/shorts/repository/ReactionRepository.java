package com.ohgoodteam.ohgoodpay.shorts.repository;

import java.util.Optional;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.shorts.repository.feed.ReactionRepositoryCustom;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ohgoodteam.ohgoodpay.common.entity.ReactionEntity;
import org.springframework.transaction.annotation.Transactional;

public interface ReactionRepository extends JpaRepository<ReactionEntity, Long>, ReactionRepositoryCustom {

    // Protection 으로 반환
    interface VideoJoinRow {
        Long getCursorId();          
        String getContext();
        Long getShortsId();
        String getShortsName();
        String getShortsExplain();
        String getThumbnail();
        String getVideoName();
        Long getLikeCount();
        Long getCommentCount();
        java.time.LocalDateTime getDate();
        Long getOwnerId();
        String getOwnerName();
        String getOwnerNickname();
        String getOwnerProfileImg();
    }


    /**
     * 특정 회원이 특정 쇼츠에 특정 반응을 했는지 여부 확인
     * @param customer
     * @param shorts
     * @param type
     * @return
     */
    boolean existsByCustomerAndShortsAndReact(CustomerEntity customer, ShortsEntity shorts, String type);


    /**
     *
     * @param customer
     * @param shorts
     * @return
     */
    Optional<ReactionEntity> findByCustomerAndShorts(CustomerEntity customer, ShortsEntity shorts);

    @Modifying
    @Transactional
    @Query("""
    UPDATE ReactionEntity r
    SET r.react = :newReact
    WHERE r.customer = :customer AND r.shorts = :shorts
    """)
    int updateReaction(@Param("customer") CustomerEntity customer,
                       @Param("shorts") ShortsEntity shorts,
                       @Param("newReact") String newReact);
}
