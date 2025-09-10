package com.ohgoodteam.ohgoodpay.shorts.service.mypage;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.ohgoodteam.ohgoodpay.common.cursor.Cursor;
import com.ohgoodteam.ohgoodpay.common.cursor.CursorUtil;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.ChannelHeader;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.Owner;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.Shelf;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.ShelfPageResponse;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.UserCard;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.mypage.ShortsMypageResponseDto.VideoCard;
import com.ohgoodteam.ohgoodpay.shorts.repository.ReactionRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.ReactionRepository.VideoJoinRow;
import com.ohgoodteam.ohgoodpay.shorts.repository.SubscriptionRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.SubscriptionRepository.FollowingRow;
import com.ohgoodteam.ohgoodpay.shorts.repository.mypage.ShortsMypageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShortsMypageServiceImpl implements ShortsMypageService {
    private final CustomerRepository customerRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ReactionRepository reactionRepository;
    private final ShortsMypageRepository shortsMypageRepository;

    @Override
    public ShortsMypageResponseDto getOverview(Long userId, int limit) {
        Optional<CustomerEntity> me = customerRepository.findById(userId);

        long followingsCount = subscriptionRepository.countFollowings(userId); // 팔로잉 수
        long videosCount = shortsMypageRepository.countByCustomerCustomerId(userId); // 내 동영상 수

        ChannelHeader header = ChannelHeader.builder()
            .userId(userId)
            .displayName(me.get().getNickname())
            .avatarUrl(me.get().getProfileImg())
            .channelUrl("http://localhost:8080/api/mypage/" + userId)
            .introduce(me.get().getIntroduce())
            .subscribersCount(followingsCount)
            .videosCount(videosCount)
            .build();
        List<FollowingRow> followingPreview = subscriptionRepository.findFollowingPreview(userId, limit+1);
        Shelf<UserCard> subscriptions = toUserShelf("구독", followingPreview, limit, null);

        List<VideoJoinRow> likedPreview = reactionRepository.findLikedShortsPreview(userId, limit+1);
        Shelf<VideoCard> liked = toVideoShelf("좋아요 표시한 영상", likedPreview, limit, null);

        List<VideoJoinRow> commentedPreview = reactionRepository.findCommentedShortsPreview(userId, limit+1);
        Shelf<VideoCard> commented = toVideoShelf("댓글 단 영상", commentedPreview, limit, null);

        return ShortsMypageResponseDto.builder()
            .header(header)
            .subscriptions(subscriptions)
            .likedVideos(liked)
            .commentedVideos(commented)
            .build();


    }
    @Override
    public ShelfPageResponse<UserCard> getSubscriptions(Long userId, String cursor, int limit) {
        Cursor decoded = CursorUtil.decode(cursor);
        List<FollowingRow> rows = subscriptionRepository.findFollowingPage(userId, decoded.getLastId(), limit + 1);

        boolean hasNext = rows.size() > limit;
        if (hasNext) {
            rows = rows.subList(0, limit);
        }

        String next = null;
        if (hasNext) {
            Long lastCursorId = rows.get(rows.size() - 1).getCursorId();
            next = CursorUtil.encode(new Cursor(lastCursorId, null, null));
        }

        List<UserCard> items = rows.stream()
                .map(r -> UserCard.builder()
                        .userId(r.getFollowingId())
                        .displayName(prefer(r.getNickname(), r.getName()))
                        .avatarUrl(r.getProfileImg())
                        .build())
                .toList();

        return ShelfPageResponse.<UserCard>builder()
                .items(items)
                .hasNext(hasNext)
                .nextCursor(next)
                .build();
    }
    @Override
    public ShelfPageResponse<VideoCard> getLikedVideos(Long userId, String cursor, int limit) {
        Cursor decoded = CursorUtil.decode(cursor);
        List<VideoJoinRow> rows = reactionRepository.findLikedShortsPage(userId, decoded.getLastId(), limit + 1);

        boolean hasNext = rows.size() > limit;
        if (hasNext) {
            rows = rows.subList(0, limit);
        }

        String next = null;
        if (hasNext) {
            Long lastCursorId = rows.get(rows.size() - 1).getCursorId();
            next = CursorUtil.encode(new Cursor(lastCursorId, null, null));
        }

        List<VideoCard> items = rows.stream()
                .map(this::toVideoCardFromJoinRow)
                .toList();

        return ShelfPageResponse.<VideoCard>builder()
                .items(items)
                .hasNext(hasNext)
                .nextCursor(next)
                .build();
    }
    @Override
    public ShelfPageResponse<VideoCard> getCommentedVideos(Long userId, String cursor, int limit) {
        Cursor decoded = CursorUtil.decode(cursor);
        List<VideoJoinRow> rows = reactionRepository.findCommentedShortsPage(userId, decoded.getLastId(), limit + 1);

        boolean hasNext = rows.size() > limit;
        if (hasNext) {
            rows = rows.subList(0, limit);
        }

        String next = null;
        if (hasNext) {
            Long lastCursorId = rows.get(rows.size() - 1).getCursorId();
            next = CursorUtil.encode(new Cursor(lastCursorId, null, null));
        }

        List<VideoCard> items = rows.stream()
                .map(this::toVideoCardFromJoinRow)
                .toList();

        return ShelfPageResponse.<VideoCard>builder()
                .items(items)
                .hasNext(hasNext)
                .nextCursor(next)
                .build();
    }

    private Shelf<UserCard> toUserShelf(String title, List<FollowingRow> raw, int limit, String seeAllPath) {
        boolean hasNext = raw.size() > limit;
        if (hasNext) raw = raw.subList(0, limit);
        String next = null;
        if(hasNext) {
            Long lastCursorId = raw.get(raw.size() - 1).getCursorId();
            next = CursorUtil.encode(new Cursor(lastCursorId, null, null));
        }
        List<UserCard> items = raw.stream()
            .map(r -> UserCard.builder()
                .userId(r.getFollowingId())
                .displayName(r.getNickname())
                .avatarUrl(r.getProfileImg())
                .build())
            .toList();
        return Shelf.<UserCard>builder()
            .title(title)
            .items(items)
            .hasNext(hasNext)
            .nextCursor(next)
            .pageSize(items.size())
            .seeAllPath(seeAllPath)
            .build();
    }
    private Shelf<VideoCard> toVideoShelf(String title, List<VideoJoinRow> raw, int limit, String seeAllPath) {
        boolean hasNext = raw.size() > limit;
        if (hasNext) {
            raw = raw.subList(0, limit);
        }

        String next = null;
        if (hasNext) {
            Long lastCursorId = raw.get(raw.size() - 1).getCursorId();
            next = CursorUtil.encode(new Cursor(lastCursorId, null, null));
        }

        List<VideoCard> items = raw.stream()
                .map(this::toVideoCardFromJoinRow)
                .toList();

        return Shelf.<VideoCard>builder()
                .title(title)
                .items(items)
                .hasNext(hasNext)
                .nextCursor(next)
                .pageSize(items.size())
                .seeAllPath(seeAllPath)
                .build();
    }
    private VideoCard toVideoCardFromJoinRow(VideoJoinRow r) { // 엔티티 -> DTO
        return VideoCard.builder()
                .videoId(r.getShortsId())
                .title(r.getShortsName())
                .content(r.getShortsExplain())
                .thumbnailUrl(r.getThumbnail())
                .videoUrl(r.getVideoName())
                .likeCount(r.getLikeCount())
                .commentCount(r.getCommentCount())
                .createdAt(r.getDate())
                .context(r.getContext())
                .owner(Owner.builder()
                        .userId(r.getOwnerId())
                        .displayName(prefer(r.getOwnerNickname(), r.getOwnerName()))
                        .avatarUrl(r.getOwnerProfileImg())
                        .build())
                .build();
    }
    
    private String prefer(String nickname, String name) {
        return (nickname != null && !nickname.isBlank()) ? nickname : name;
    }

}