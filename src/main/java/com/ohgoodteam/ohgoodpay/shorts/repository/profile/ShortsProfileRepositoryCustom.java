package com.ohgoodteam.ohgoodpay.shorts.repository.profile;

public interface ShortsProfileRepositoryCustom {

    //프로필 수정 (이미지 포함)
    int updateProfileWithImage(Long customerId, String nickname, String introduce, String profileImg);
    //프로필 수정 (이미지 제외)
    int updateProfileWithoutImage(Long customerId, String nickname, String introduce);
}
