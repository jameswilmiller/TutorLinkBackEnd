package com.tl.tutor_link.config;

import io.github.bucket4j.Bucket;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> enquiryBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> uploadBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    // 5 enquiries per hour
    private Bucket newEnquiryBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(5)
                        .refillIntervally(5, Duration.ofHours(1))
                        .build())
                .build();
    }

    // 10 auth attempts per 15 minutes
    private Bucket newAuthBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(10)
                        .refillIntervally(10, Duration.ofMinutes(15))
                        .build())
                .build();
    }

    // 20 uploads per hour
    private Bucket newUploadBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(20)
                        .refillIntervally(20, Duration.ofHours(1))
                        .build())
                .build();
    }

    // 200 requests per minute
    private Bucket newGeneralBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(200)
                        .refillIntervally(200, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String key = request.getRemoteAddr();

        Bucket bucket;

        if (path.matches("/tutors/\\d+/enquire")) {
            bucket = enquiryBuckets.computeIfAbsent(key, k -> newEnquiryBucket());
        } else if (path.startsWith("/auth/")) {
            bucket = authBuckets.computeIfAbsent(key, k -> newAuthBucket());
        } else if (path.startsWith("/upload/")) {
            bucket = uploadBuckets.computeIfAbsent(key, k -> newUploadBucket());
        } else {
            bucket = generalBuckets.computeIfAbsent(key, k -> newGeneralBucket());
        }

        if (!bucket.tryConsume(1)) {
            response.setStatus(429);
            response.getWriter().write("Too many requests. Please try again later.");
            return;
        }

        chain.doFilter(request, response);
    }
}