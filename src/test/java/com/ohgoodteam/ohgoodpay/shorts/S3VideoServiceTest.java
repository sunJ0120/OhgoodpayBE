package com.ohgoodteam.ohgoodpay.shorts;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.ohgoodteam.ohgoodpay.shorts.service.S3VideoService;

@SpringBootTest
public class S3VideoServiceTest {
    @Autowired
    private S3VideoService s3VideoService;

    @Test
    void testRealVideoUpload() throws Exception {
        // 실제 동영상 파일 경로
        Path filePath = Paths.get("C:\\samplevideo.mp4");
        
        // 파일 존재 확인
        assertTrue(Files.exists(filePath), "파일이 존재하지 않습니다: " + filePath);
        
        // 파일을 MultipartFile로 변환
        MultipartFile file = new MockMultipartFile(
            "file", 
            "samplevideo.mp4", 
            "video/mp4", 
            Files.readAllBytes(filePath)
        );

        s3VideoService.upload(file, "hwajun");
        
        // assertNotNull(result);
        // assertNotNull(result.key());
        //assertNotNull(result.downloadUrl());
        
        System.out.println("업로드 성공!");
        // System.out.println("Key: " + result.key());
        //System.out.println("Download URL: " + result.downloadUrl());
    }
}
