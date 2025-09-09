package com.ohgoodteam.ohgoodpay.shorts.controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ohgoodteam.ohgoodpay.shorts.dto.request.ShortsUploadRequestDto;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ShortsUploadResponseDto;
import com.ohgoodteam.ohgoodpay.shorts.service.ShortsUploadService;
import lombok.RequiredArgsConstructor;

@CrossOrigin(
    origins = "http://localhost:5173",
    allowCredentials = "true"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShortsUploadController {
    private final ShortsUploadService s3VideoService;

    @PostMapping("/upload")
    public ResponseEntity<ShortsUploadResponseDto> upload(
        @RequestPart("video") MultipartFile video, 
        @RequestPart(value="thumbnail", required = false) MultipartFile thumbnail,
        @RequestParam("title") String title,
        @RequestParam("content") String content
    ) {
        // String title = requestDto.getTitle();
        // String content = requestDto.getContent();
        try {
            ShortsUploadResponseDto response = s3VideoService.upload(video, thumbnail, title, content);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 공용 error dto 구현되면 그에 맞게
            return ResponseEntity.status(500).body(
                ShortsUploadResponseDto.builder()
                    .success(false)
                    .message("업로드 실패: " + e.getMessage())
                    .build()
            );
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
