package com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortsMypageResponseDto {
    private ChannelHeader header;
    private Shelf<UserCard> subscriptions;     
    private Shelf<VideoCard> likedVideos;      
    private Shelf<VideoCard> commentedVideos;  

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ChannelHeader {
        private Long userId;
        private String displayName;   // 닉네임
        private String avatarUrl;
        private String channelUrl;    // 사용자 페이지 링크
        private String introduce;
        private long subscribersCount; 
        private long videosCount;      
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Shelf<T> {
        private String title;         // "구독", "좋아요 표시한 영상", "댓글 단 영상"
        private List<T> items;
        private boolean hasNext;      
        private String nextCursor;    
        private int pageSize;         
        private String seeAllPath;    
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserCard {
        private Long userId;
        private String displayName;
        private String avatarUrl;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VideoCard {
        private Long videoId;
        private String title;
        private String content;
        private String thumbnailUrl; 
        private String videoUrl;      
        private Long likeCount; 
        private Long commentCount; 

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;
        private Owner owner; 
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Owner {
        private Long userId;
        private String displayName;
        private String avatarUrl;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ShelfPageResponse<T> { // 전체보기 최종 응답
        private List<T> items;
        private boolean hasNext;
        private String nextCursor;
    }
}
