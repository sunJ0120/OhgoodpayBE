package com.ohgoodteam.ohgoodpay.shorts.dto.response.feed;

import com.ohgoodteam.ohgoodpay.common.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortsCommentDataDTO {
    /**
     * 전체 댓글 정보 DTO
     */
    // 댓글 정보
    private Long commentId;
    private Long customerId;
    private Long shortsId;
    private Long gno;
    private String content;
    private LocalDateTime date;

    // 작성자 정보
    private String nickname;
    private String profileImg;

    public ShortsCommentDataDTO(CommentEntity commentEntity){
        this.commentId = commentEntity.getCommentId();
        this.customerId = commentEntity.getCustomer().getCustomerId();
        this.shortsId = commentEntity.getShorts().getShortsId();
        this.gno = commentEntity.getGno();
        this.content = commentEntity.getContent();
        this.date = commentEntity.getDate();
        this.nickname = commentEntity.getCustomer().getNickname();
        this.profileImg = commentEntity.getCustomer().getProfileImg();
    }
}
