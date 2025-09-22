package com.ohgoodteam.ohgoodpay.shorts.service.profile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.profile.ShortsProfileEditResponseDto;
import com.ohgoodteam.ohgoodpay.shorts.repository.profile.ShortsProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShortsProfileServiceImpl implements ShortsProfileService {
    private final ShortsProfileRepository shortsProfileRepository;
    private final S3Client s3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    // 프로필 수정, s3에 이미지 업로드
    @Override
    @Transactional
    public ShortsProfileEditResponseDto editProfile(Long customerId, String nickname, String introduce, MultipartFile profileImg) {
        try {
            String profileImgKey = null;
            
            // 프로필 이미지가 있으면 s3에 업로드
            if (profileImg != null && !profileImg.isEmpty()) {
                profileImgKey = uploadProfileImageToS3(profileImg);
            }
            
            // db에 업데이트
            int result = shortsProfileRepository.updateProfile(customerId, nickname, introduce, profileImgKey);
            
            if (result > 0) {
                return new ShortsProfileEditResponseDto("true", "프로필이 성공적으로 수정되었습니다.");
            } else {
                return new ShortsProfileEditResponseDto("false", "프로필 수정에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("프로필 수정 실패: customerId={}", customerId, e);
            return new ShortsProfileEditResponseDto("false", "프로필 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    // 프로필 이미지 s3에 업로드 
    private String uploadProfileImageToS3(MultipartFile profileImg) throws IOException {
        // UUID + "_" + sample.jpg
        String originalFilename = profileImg.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : ".jpg";
        String profileImgKey = UUID.randomUUID() + "_" + 
            (originalFilename != null ? originalFilename : "profile" + extension);
        
        // S3 업로드 요청 
        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(profileImgKey)
            .contentType(profileImg.getContentType() != null ? profileImg.getContentType() : "image/jpeg")
            .build();
        
        // S3에 업로드
        try (InputStream inputStream = profileImg.getInputStream()) {
            s3.putObject(putRequest, RequestBody.fromInputStream(inputStream, profileImg.getSize()));
        }
        
        log.info("프로필 이미지 S3 업로드 완료: {}", profileImgKey);
        return profileImgKey;
    }
}
