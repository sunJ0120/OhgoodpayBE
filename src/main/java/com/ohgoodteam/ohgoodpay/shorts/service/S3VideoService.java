package com.ohgoodteam.ohgoodpay.shorts.service;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ohgoodteam.ohgoodpay.shorts.config.S3Config;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
// import software.amazon.awssdk.services.s3.model.AmazonS3;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3VideoService {
    private final S3Client s3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.region}")
    private String region;

    public void upload(MultipartFile file, String ownerId) throws IOException {
       // String ext = getExt(file.getOriginalFilename()); // 확장자 추출
        String key = UUID.randomUUID() + "_" + file.getOriginalFilename();
        System.out.println("key: " + key);

        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
                .build();

        try {
            s3.putObject(put, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (S3Exception e) {
            throw new IOException("S3 upload failed: " + e.awsErrorDetails().errorMessage(), e);
        }

        // 15분 동안 유효한 다운로드 URL 생성 (버킷은 private 유지)
        // String downloadUrl = presignedGetUrl(key, Duration.ofMinutes(15));


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

    // private String getExt(String name) {
    //     if (name == null) return "";
    //     int dot = name.lastIndexOf('.');
    //     return dot < 0 ? "" : name.substring(dot + 1).toLowerCase();
    // }

    // private String presignedGetUrl(String key, Duration ttl) {
    //     try (S3Presigner presigner = S3Presigner.builder()
    //             .region(software.amazon.awssdk.regions.Region.of(region))
    //             .credentialsProvider(software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider.create())
    //             .build()) {

    //         GetObjectRequest get = GetObjectRequest.builder()
    //                 .bucket(bucket).key(key).build();

    //         return presigner.presignGetObject( //실제 url 생성
    //                 GetObjectPresignRequest.builder()
    //                         .signatureDuration(ttl)
    //                         .getObjectRequest(get)
    //                         .build()
    //         ).url().toString();
    //     }
    // }

    public record UploadResult(String key, String downloadUrl) {}
}
