package com.ohgoodteam.ohgoodpay.shorts.repository;

import com.ohgoodteam.ohgoodpay.common.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // 특정 쇼츠의 모든 댓글 조회
    @Query("""
        SELECT c FROM CommentEntity c
        JOIN FETCH c.customer cu
        WHERE c.shorts.shortsId = :shortsId
        ORDER BY c.date ASC
    """)
    List<CommentEntity> findAllComments(Long shortsId);

}
