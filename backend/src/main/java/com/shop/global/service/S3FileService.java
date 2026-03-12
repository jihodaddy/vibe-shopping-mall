package com.shop.global.service;

import com.shop.global.dto.PresignedUrlResponse;
import com.shop.global.exception.BusinessException;
import com.shop.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.cdn-domain:}")
    private String cdnDomain;

    @Value("${cloud.aws.endpoint:}")
    private String endpoint;

    public PresignedUrlResponse generatePresignedUrl(String fileName, String contentType) {
        String key = "products/" + UUID.randomUUID() + "/" + fileName;

        try {
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                req -> req.signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(put -> put
                        .bucket(bucket)
                        .key(key)
                        .contentType(contentType)
                        .build())
            );

            String fileUrl;
            if (cdnDomain != null && !cdnDomain.isBlank()) {
                fileUrl = "https://" + cdnDomain + "/" + key;
            } else if (endpoint != null && !endpoint.isBlank()) {
                fileUrl = endpoint + "/" + bucket + "/" + key;
            } else {
                fileUrl = "https://" + bucket + ".s3.amazonaws.com/" + key;
            }

            return new PresignedUrlResponse(presignedRequest.url().toString(), fileUrl);
        } catch (AwsServiceException e) {
            log.error("S3 presigned URL 생성 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }
}
