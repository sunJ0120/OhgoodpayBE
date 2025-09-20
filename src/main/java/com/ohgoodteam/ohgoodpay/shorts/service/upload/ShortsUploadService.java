package com.ohgoodteam.ohgoodpay.shorts.service.upload;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.upload.ShortsUploadResponseDto;

public interface ShortsUploadService {
    
    // 비디오 업로드
    ShortsUploadResponseDto upload(MultipartFile file, MultipartFile thumbnail, String title, String content) throws IOException;
    
    // 파일 삭제
    void delete(String fileName) throws IOException;
}