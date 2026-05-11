package com.tl.tutor_link.image.service;

import com.tl.tutor_link.common.exception.FileUploadException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import com.tl.tutor_link.common.config.AppConstants;
@Component
public class ImageValidator {



    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Tika tika = new Tika();

    /**
     * Detects the actual content type of a file by inspecting its bytes,
     * and verifies it matches one of allowed image types.
     */
    public void verifyContentType(MultipartFile file) throws IOException {
        String detectedType;
        try (InputStream in = file.getInputStream()) {
            detectedType = tika.detect(in);
        }

        if (!ALLOWED_TYPES.contains(detectedType)) {
            throw new FileUploadException(
                    "File is not a valid JPEG, PNG, or WebP image (detected: " + detectedType + ")"
            );
        }
    }

    /**
     * Decodes the image, validates dimensions, and re-encodes as a clean JPEG.
     * This strips any embedded metadata, EXIF data, or potential payloads,
     * l
     */
    public byte[] reencodeAsJpeg(MultipartFile file) throws IOException {
        BufferedImage image;
        try (InputStream in = new ByteArrayInputStream(file.getBytes())) {
            image = ImageIO.read(in);
        }

        if (image == null) {
            throw new FileUploadException("File could not be decoded as an image");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        if (width > AppConstants.MAX_IMAGE_DIMENSION|| height > AppConstants.MAX_IMAGE_DIMENSION) {
            throw new FileUploadException("Image dimensions are too large (max " + AppConstants.MAX_IMAGE_DIMENSION + "px)");
        }
        if (width < AppConstants.MIN_IMAGE_DIMENSION || height < AppConstants.MIN_IMAGE_DIMENSION) {
            throw new FileUploadException("Image dimensions are too small (min " + AppConstants.MIN_IMAGE_DIMENSION + "px)");
        }

        // Convert to RGB if necessary (PNGs may have alpha channels JPEG can't handle)
        if (image.getType() != BufferedImage.TYPE_INT_RGB) {
            BufferedImage rgb = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            rgb.getGraphics().drawImage(image, 0, 0, null);
            image = rgb;
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            boolean success = ImageIO.write(image, "jpg", out);
            if (!success) {
                throw new FileUploadException("Failed to re-encode image as JPEG");
            }
            return out.toByteArray();
        }
    }
}