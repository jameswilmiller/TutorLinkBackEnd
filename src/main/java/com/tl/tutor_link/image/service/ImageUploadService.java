package com.tl.tutor_link.image.service;

import com.tl.tutor_link.common.exception.FileUploadException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import com.tl.tutor_link.common.config.AppConstants;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
public class ImageUploadService {
    private static final Logger log = LoggerFactory.getLogger(ImageUploadService.class);
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ImageValidator imageValidator;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );
    public ImageUploadService(S3Client s3Client, S3Presigner s3Presigner, ImageValidator imageValidator) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.imageValidator = imageValidator;
    }

    @Transactional
    public String uploadProfileImage(MultipartFile file, Long userId) throws IOException {
        log.info("User {} uploading profile image: {} bytes, type {}",
                userId, file.getSize(), file.getContentType());

        // layer 1: check the size, declared type and not empty
        validateFile(file);

        // layer 2: verify content matches declares type using Tika
        imageValidator.verifyContentType(file);

        // layer 3: decode + dimension check + re-encode as clean JPEG
        byte[] cleanBytes = imageValidator.reencodeAsJpeg(file);



        String key = AppConstants.S3_PROFILE_IMAGES_PREFIX + "user-" + userId + "/" + UUID.randomUUID() + ".jpg";

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(file.getBytes())
        );
        log.info("Profile image uploaded: {}", key);
        return key;
    }

    public String getPresignedUrl(String key) {
        if (key == null || key.isBlank()) return null;

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(AppConstants.S3_PRESIGNED_URL_TTL)
                .getObjectRequest(getRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File is required");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new FileUploadException("File type is not allowed. Use JPEG, PNG or WebP");
        }

        if (file.getSize() > AppConstants.MAX_IMAGE_SIZE_BYTES) {
            throw new FileUploadException("File is too large, must be less than 5MB");
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf("."));
    }

    public void deleteImage(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            log.debug("S3 object deleted: {}", key);
        } catch (Exception e) {
            log.warn("Failed to delete S3 object: {}", key, e);
        }
    }
}
