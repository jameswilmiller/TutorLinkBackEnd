package com.tl.tutor_link.service;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Set;

public class ImageUploadService {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public ImageUploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }
}
