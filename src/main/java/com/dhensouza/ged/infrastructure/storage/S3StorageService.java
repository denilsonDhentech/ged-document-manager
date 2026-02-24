package com.dhensouza.ged.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.time.Duration;

@Service
public class S3StorageService {

    private final S3Client s3Client;
    private final String bucketName;

    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String region;

    private final S3Presigner s3Presigner;

    public S3StorageService(
            S3Client s3Client,
            @Value("${storage.s3.bucket-name}") String bucketName,
            @Value("${storage.s3.endpoint}") String endpoint,
            @Value("${storage.s3.access-key}") String accessKey,
            @Value("${storage.s3.secret-key}") String secretKey,
            @Value("${storage.s3.region}") String region,
            S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.s3Presigner = s3Presigner;
    }

    public void upload(String fileKey, byte[] data, String contentType) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
    }

    public String generatePresignedUrl(String fileKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}