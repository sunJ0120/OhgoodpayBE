package com.ohgoodteam.ohgoodpay.shorts.repository.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;

@Repository
public interface ShortsProfileRepository extends JpaRepository<CustomerEntity, Long> {
    
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE customer
        SET nickname = :nickname, introduce = :introduce, profile_img = :profileImg
        WHERE customer_id = :customerId
        """, nativeQuery = true)
    int updateProfile(@Param("customerId") Long customerId,
                     @Param("nickname") String nickname,
                     @Param("introduce") String introduce,
                     @Param("profileImg") String profileImg);
}
