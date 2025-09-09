package com.ohgoodteam.ohgoodpay.recommend.service;

import com.ohgoodteam.ohgoodpay.common.repository.CustomerRepository;
import com.ohgoodteam.ohgoodpay.recommend.dto.dashdto.ProfileSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {
    private final CustomerRepository customerRepository;

    @Override
    public ProfileSummaryDTO getProfile(Long customerId) {
        var c = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 고객입니다."));

        String username = (c.getNickname() != null && !c.getNickname().isBlank())
                ? c.getNickname() : c.getName();

        // GradeEntity → gradeName(문자열) 추출 후 정규화
        String gradeCode = (c.getGrade() != null) ? c.getGrade().getGradeName() : null;
        String grade = normalizeGrade(gradeCode);

        int score = c.getScore(); // primitive, null 체크 불필요

        return ProfileSummaryDTO.builder()
                .customerId(customerId)
                .username(username)
                .grade(grade)
                .score(score) // Integer로 자동 박싱
                .build();
    }

    private String normalizeGrade(String raw) {
        if (raw == null) return "UNKNOWN";
        String s = raw.trim();
        return s.isEmpty() ? "UNKNOWN" : s.toUpperCase();
    }
}
