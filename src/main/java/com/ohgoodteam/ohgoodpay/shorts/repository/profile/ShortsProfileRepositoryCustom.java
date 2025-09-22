package com.ohgoodteam.ohgoodpay.shorts.repository.profile;

public interface ShortsProfileRepositoryCustom {
    
    //프로필 수정
    int updateProfile(Long customerId, String nickname, String introduce, String profileImg);
}
