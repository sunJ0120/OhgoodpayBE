package com.ohgoodteam.ohgoodpay.shorts.controller;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ohgoodteam.ohgoodpay.common.FileSystemMultipartFile;
import com.ohgoodteam.ohgoodpay.shorts.service.S3VideoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class VideoUploadController {
    private final S3VideoService s3VideoService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload() throws Exception {
        try {
            Path filePath = Paths.get("C:\\samplevideo.mp4");
        
            // 파일 존재 확인
            // assertTrue(Files.exists(filePath), "파일이 존재하지 않습니다: " + filePath);
            
            MultipartFile file = new FileSystemMultipartFile(filePath);
    
            s3VideoService.upload(file, "hwajun");
            return ResponseEntity.ok("업로드 성공");
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("업로드 실패");
        }
        
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete() throws Exception {
        try {
            // 실제로는 shorts_id를 클라이언트에서 받아서 그것에 해당하는 aws key값(video_name)을 조회후 삭제요청
            Path filePath = Paths.get("C:\\samplevideo.mp4");
            String fileName = filePath.getFileName().toString(); 
            System.out.println("fileName: " + fileName);
            s3VideoService.delete(fileName);
            return ResponseEntity.ok("삭제 성공");
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("삭제 실패");
        }
    }
}
