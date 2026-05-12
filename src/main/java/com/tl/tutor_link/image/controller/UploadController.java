package com.tl.tutor_link.image.controller;

import com.tl.tutor_link.image.service.ImageUploadService;
import com.tl.tutor_link.tutor.service.TutorService;
import com.tl.tutor_link.user.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.io.IOException;
import java.util.Map;

/**
 * File upload endpoints. Currently only supports profile images.
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    private final TutorService tutorService;

    public UploadController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping("/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user
    ) throws IOException {
        return ResponseEntity.ok(tutorService.replaceProfileImage(user, file));
    }
}