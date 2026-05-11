package com.tl.tutor_link.common.config;

import java.time.Duration;

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

    // S3 presigned URLs
    public static final Duration S3_PRESIGNED_URL_TTL = Duration.ofHours(1);
}
