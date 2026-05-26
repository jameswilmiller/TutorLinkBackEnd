package com.tl.tutor_link.common.config;

import java.time.Duration;
import java.util.Set;

/**
 * Application-wide constants. Centralised here so values aren't scattered
 * as magic numbers across the codebase.
 */
public class AppConstants {
    private AppConstants() {
        // utility class
    }

    // image upload
    public static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024;
    public static final int MAX_IMAGE_DIMENSION = 4000;
    public static final int MIN_IMAGE_DIMENSION = 50;
    public static final String S3_PROFILE_IMAGES_PREFIX = "profile-images/";

    // Tutor Search
    public static final double MAX_SEARCH_DISTANCE_KM = 20.0;
    public static final int DEFAULT_PAGE_SIZE = 20;

    // Verification
    public static final Duration VERIFICATION_CODE_TTL = Duration.ofMinutes(15);
    public static final int VERIFICATION_CODE_MIN = 100000;
    public static final int VERIFICATION_CODE_RANGE = 900000;

    // S3 presigned URLs
    public static final Duration S3_PRESIGNED_URL_TTL = Duration.ofHours(1);

    // Email
    public static final String SMTP_HOST = "smtp.gmail.com";
    public static final int SMTP_PORT = 587;

    // JWT Types
    public static final String CLAIM_TOKEN_TYPE = "tokenType";
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";


    // Auth
    public static final String AUTH_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";


    // Image
    public static final Set<String> ALLOWED_IMAGE_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );
    public static final String JPEG_CONTENT_TYPE = "image/jpeg";
    public static final String JPEG_EXTENSION = ".jpg";
}
