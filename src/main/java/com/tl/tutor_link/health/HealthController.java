package com.tl.tutor_link.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/sentry-test")
    public ResponseEntity<String> sentryTest() {
        throw new RuntimeException("Sentry test - this should appear in the Sentry dashboard");
    }
}