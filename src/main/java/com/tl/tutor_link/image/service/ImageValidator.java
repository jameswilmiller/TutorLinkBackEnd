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
/**
 * Validates and sanitises uploaded images. Two operations: verifyContentType
 * confirms the file's actual bytes match a supported image format using Apache
 * Tika to detect type, and reencodeAsJpeg decodes the image, enforces dimension
 * limits, and re-encodes to a fresh JPEG, stripping EXIF metadata and any
 * embedded payloads.
 */
@Component
public class ImageValidator {

    private final Tika tika = new Tika();

    public void verifyContentType(MultipartFile file) throws IOException {
        String detectedType;
        try (InputStream in = file.getInputStream()) {
            detectedType = tika.detect(in);
        }

        if (!AppConstants.ALLOWED_IMAGE_CONTENT_TYPES.contains((detectedType))) {
            throw new FileUploadException(
                    "File is not a valid JPEG, PNG, or WebP image (detected: " + detectedType + ")"
            );
        }
    }

    public byte[] reencodeAsJpeg(MultipartFile file) throws IOException {
        BufferedImage image = decode(file);
        validateDimensions(image);
        BufferedImage rgb = ensureRgb(image);
        return encodeAsJpeg(rgb);
    }

    private BufferedImage decode(MultipartFile file) throws IOException {
        BufferedImage image;
        try (InputStream in = new ByteArrayInputStream(file.getBytes())) {
            image = ImageIO.read(in);
        }

        if (image == null) {
            throw new FileUploadException("File could not be decoded as an image");
        }
        return image;
    }

    private void validateDimensions(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > AppConstants.MAX_IMAGE_DIMENSION || height > AppConstants.MAX_IMAGE_DIMENSION) {
            throw new FileUploadException(
                    "Image dimensions are too large (max " + AppConstants.MAX_IMAGE_DIMENSION + "px)"
            );
        }
        if (width < AppConstants.MIN_IMAGE_DIMENSION || height < AppConstants.MIN_IMAGE_DIMENSION) {
            throw new FileUploadException(
                    "Image dimensions are too small (min " + AppConstants.MIN_IMAGE_DIMENSION + "px)"
            );
        }
    }

    private BufferedImage ensureRgb(BufferedImage image) {
        if (image.getType() == BufferedImage.TYPE_INT_RGB) {
            return image;
        }
        BufferedImage rgb = new BufferedImage(
                image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB
        );
        rgb.getGraphics().drawImage(image, 0, 0, null);
        return rgb;
    }

    private byte[] encodeAsJpeg(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            boolean success = ImageIO.write(image, "jpg", out);
            if (!success) {
                throw new FileUploadException("Failed to re-encode image as JPEG");
            }
            return out.toByteArray();
        }
    }

}