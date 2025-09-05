package com.ohgoodteam.ohgoodpay.shorts.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ohgoodteam.ohgoodpay.common.entity.CustomerEntity;
import com.ohgoodteam.ohgoodpay.common.entity.ShortsEntity;
import com.ohgoodteam.ohgoodpay.shorts.dto.response.ShortsUploadResponseDto;
import com.ohgoodteam.ohgoodpay.shorts.repository.ShortsUploadRepository;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@RequiredArgsConstructor
public class ShortsUploadService {
    private final S3Client s3;
    private final ShortsUploadRepository videoUploadRepository;
    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    @Transactional
    public ShortsUploadResponseDto upload(
        MultipartFile file, 
        MultipartFile thumbnail, 
        String title, 
        String content
    ) throws IOException {
        // 일단은 파일 해상도가 760p 이상이면 업로드 막기 -> 시간남으면 1080p 이상 들어오면 760p로 변환
        if (file.isEmpty() || thumbnail.isEmpty()) throw new IllegalArgumentException("파일을 다시 업로드해주세요.");
        if (file.getSize() > 200 * 1024 * 1024) throw new IllegalArgumentException("허용용량이 초과되었습니다.");
        
        Path tmp = Files.createTempFile("upload-", ".mp4");
        file.transferTo(tmp.toFile());
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "ffprobe",
                "-v", "error",
                "-select_streams", "v:0",
                "-show_entries", "stream=width,height:format=duration", // 해상도, 영상길이 
                "-of", "csv=p=0",
                tmp.toAbsolutePath().toString()
            );
            pb.redirectErrorStream(true);
            Process process = pb.start(); // ffprobe 실행
            String output;

            try (InputStream is = process.getInputStream()) {
                output = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
            }
            process.waitFor();

            String[] parts = output.split(","); // 파일 정보 파싱
            int width = Integer.parseInt(parts[0]); // 해상도
            int height = Integer.parseInt(parts[1]); //해상도
            double duration = Double.parseDouble(parts[2]); // 영상 길이

            if (width > 760 || height > 760) throw new IllegalArgumentException("해상도가 760p 이상이면 업로드할 수 없습니다.");
            if (duration > 60) throw new IllegalArgumentException("영상 길이가 60초 이상이면 업로드할 수 없습니다.");

        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("비디오 분석 중 중단되었습니다.", e);
        } finally {
            try {
                Files.deleteIfExists(tmp);
            } catch (Exception ignore) {}
        }

        String videoKey = UUID.randomUUID() + "_" + file.getOriginalFilename(); // 영상
        System.out.println("영상 key: " + videoKey);

        PutObjectRequest videoPut = PutObjectRequest.builder()
                .bucket(bucket)
                .key(videoKey)
                .contentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
                .build();
        try {
            s3.putObject(videoPut, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (S3Exception e) {
            throw new IOException("S3 upload failed: " + e.awsErrorDetails().errorMessage(), e);
        }


        String thumbKey = UUID.randomUUID() + "_" + thumbnail.getOriginalFilename(); // 썸네일 
        System.out.println("썸네일 key: " + thumbKey);

        PutObjectRequest thumbPut = PutObjectRequest.builder()
                .bucket(bucket)
                .key(thumbKey)
                .contentType(thumbnail.getContentType() == null ? "image/jpeg" : thumbnail.getContentType())
                .build();
        try {
            s3.putObject(thumbPut, RequestBody.fromInputStream(thumbnail.getInputStream(), thumbnail.getSize()));
        } catch (S3Exception e) {
            throw new IOException("S3 upload failed: " + e.awsErrorDetails().errorMessage(), e);
        }

        ShortsEntity shortsEntity = ShortsEntity.builder()
                .videoName(videoKey)
                .thumbnail(thumbKey)
                .shortsName(title)
                .shortsExplain(content)
                .date(LocalDateTime.now())
                .likeCount(0)
                .commentCount(0)
                .customer(CustomerEntity.builder().customerId(1L).build())
                .build();
        videoUploadRepository.save(shortsEntity);
        
        return ShortsUploadResponseDto.builder()
                .success(true)
                .message("업로드가 성공적으로 완료되었습니다")
                .build();
    }

    public void delete(String fileName) throws IOException { 
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
            .bucket(bucket)
            .build();

        ListObjectsV2Response listRes = s3.listObjectsV2(listReq);

        listRes.contents().stream()
                .map(S3Object::key)
                .filter(key -> key.endsWith(fileName)) 
                .findFirst()
                .ifPresent(key -> {
                    s3.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build());
                    System.out.println("삭제 완료: " + key);
                });
    }

}
