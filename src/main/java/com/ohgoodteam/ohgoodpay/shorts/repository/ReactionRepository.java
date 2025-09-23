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

    // 별도의 DTO로 매핑할 수도 있지만 화면,응답에 필요한 필드만 선택적으로 매핑하기 위해서.  DTO 파일을 따로 안만드는게 장점. 
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class VideoJoinRow {
        private Long cursorId;          
        private String context;
        private Long shortsId;
        private String shortsName;
        private String shortsExplain;
        private String thumbnail;
        private String videoName;
        private long likeCount;        
        private long commentCount;     
        private java.time.LocalDateTime date;
        private Long ownerId;
        private String ownerName;
        private String ownerNickname;
        private String ownerProfileImg;
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
