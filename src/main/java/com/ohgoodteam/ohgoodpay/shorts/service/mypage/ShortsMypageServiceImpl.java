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
import com.ohgoodteam.ohgoodpay.shorts.repository.ShortsMypageRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.ReactionRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.ReactionRepository.VideoJoinRow;
import com.ohgoodteam.ohgoodpay.shorts.repository.SubscriptionRepository;
import com.ohgoodteam.ohgoodpay.shorts.repository.SubscriptionRepository.FollowingRow;
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

    // 마이페이지 미리보기
    @Override
    public ShortsMypageResponseDto getOverview(Long userId, int limit) {
        Optional<CustomerEntity> me = customerRepository.findById(userId);

        long followingsCount = subscriptionRepository.countFollowings(userId); // 팔로잉 수
        long videosCount = shortsMypageRepository.countByCustomerCustomerId(userId); // 내 동영상 수

        // 채널 헤더 생성 (ShortsMypageResponseDto.ChannelHeader)
        ChannelHeader header = ChannelHeader.builder()
            .userId(userId)
            .displayName(me.get().getNickname())
            .avatarUrl(me.get().getProfileImg())
            .channelUrl("http://localhost:8080/api/mypage/" + userId)
            .introduce(me.get().getIntroduce())
            .subscribersCount(followingsCount)
            .videosCount(videosCount)
            .build();

        // 구독 미리보기
        List<FollowingRow> followingPreview = subscriptionRepository.findFollowingPreview(userId, limit+1);
        // 구독 미리보기 Shelf 객체 생성 (ShortsMypageResponseDto.Shelf)
        Shelf<UserCard> subscriptions = toUserShelf("구독", followingPreview, limit, null);

        // 좋아요 한 영상 미리보기
        List<VideoJoinRow> likedPreview = reactionRepository.findLikedShortsPreview(userId, limit+1);
        // 좋아요 한 영상 미리보기 Shelf 객체 생성 (ShortsMypageResponseDto.Shelf)
        Shelf<VideoCard> liked = toVideoShelf("좋아요 표시한 영상", likedPreview, limit, null);

        // 댓글 단 영상 미리보기
        List<VideoJoinRow> commentedPreview = reactionRepository.findCommentedShortsPreview(userId, limit+1);
        // 댓글 단 영상 미리보기 Shelf 객체 생성 (ShortsMypageResponseDto.Shelf)
        Shelf<VideoCard> commented = toVideoShelf("댓글 단 영상", commentedPreview, limit, null);

        // 미리보기 최종 응답 객체 생성 (ShortsMypageResponseDto)
        return ShortsMypageResponseDto.builder()
            .header(header)
            .subscriptions(subscriptions)
            .likedVideos(liked)
            .commentedVideos(commented)
            .build();

    }

    // 구독 전체보기
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

        // 구독 전체보기 최종 응답 객체 생성 (ShortsMypageResponseDto.ShelfPageResponse)
        return ShelfPageResponse.<UserCard>builder()
                .items(items)
                .hasNext(hasNext)
                .nextCursor(next)
                .build();
    }

    // 좋아요 한 영상 전체보기
    @Override
    public ShelfPageResponse<VideoCard> getLikedVideos(Long userId, String cursor, int limit) {
        Cursor decoded = CursorUtil.decode(cursor);
        //  VideoJoinRow는 DB 조회 결과 저장(내부용)
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

        // VideoCard 리스트 생성 (ShortsMypageResponseDto.VideoCard)
        // VideoCard는 API 응답 데이터 (외부용)
        List<VideoCard> items = rows.stream()
                .map(this::toVideoCardFromJoinRow)
                .toList();

        // 좋아요 한 영상 전체보기 최종 응답 객체 생성 (ShortsMypageResponseDto.ShelfPageResponse)
        return ShelfPageResponse.<VideoCard>builder()
                .items(items)
                .hasNext(hasNext)
                .nextCursor(next)
                .build();
    }
    // 댓글 단 영상 전체보기
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

        // 댓글 단 영상 전체보기 최종 응답 객체 생성 (ShortsMypageResponseDto.ShelfPageResponse)
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
            Long lastCursorId = raw.get(raw.size() - 1).getCursorId(); // 마지막 커서 ID
            next = CursorUtil.encode(new Cursor(lastCursorId, null, null)); // 다음 페이지 커서 생성
        }
        // 유저 카드 리스트 생성 (ShortsMypageResponseDto.UserCard)
        List<UserCard> items = raw.stream()
            .map(r -> UserCard.builder()
                .userId(r.getFollowingId())
                .displayName(r.getNickname())
                .avatarUrl(r.getProfileImg())
                .build())
            .toList();

        // UseCard 리스트를 감싼 Shelf 객체 생성 (제목, 아이템, 다음 페이지 여부 등 페이징 정보 포함한 응답 포맷)
        return Shelf.<UserCard>builder()
            .title(title)
            .items(items)
            .hasNext(hasNext)
            .nextCursor(next)
            .pageSize(items.size())
            .seeAllPath(seeAllPath)
            .build();
    }
    // VideoCard 리스트를 감싼 Shelf 객체 생성 (제목, 아이템, 다음 페이지 여부 등 페이징 정보 포함한 응답 포맷)
    private Shelf<VideoCard> toVideoShelf(String title, List<VideoJoinRow> raw, int limit, String seeAllPath) {
        boolean hasNext = raw.size() > limit;
        if (hasNext) {
            raw = raw.subList(0, limit);
        }
        // 다음 페이지 커서 생성
        String next = null;
        if (hasNext) {
            Long lastCursorId = raw.get(raw.size() - 1).getCursorId();
            next = CursorUtil.encode(new Cursor(lastCursorId, null, null));
        }

        List<VideoCard> items = raw.stream()
                .map(this::toVideoCardFromJoinRow)
                .toList();

        // response 객체 생성
        return Shelf.<VideoCard>builder()
                .title(title)
                .items(items)
                .hasNext(hasNext)
                .nextCursor(next)
                .pageSize(items.size())
                .seeAllPath(seeAllPath)
                .build();
    }
    // VideoCard 객체 생성 (ShortsMypageResponseDto.VideoCard)
    private VideoCard toVideoCardFromJoinRow(VideoJoinRow r) { 
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
    // 닉네임, 이름 선택 로직
    private String prefer(String nickname, String name) {
        return (nickname != null && !nickname.isBlank()) ? nickname : name;
    }
    // 구독 취소 
    @Override
    public long deleteSubscription(Long userId, Long targetId) {
        long result = subscriptionRepository.deleteByFollowerCustomerIdAndFollowingCustomerId(userId, targetId);
        return result;
    }
}