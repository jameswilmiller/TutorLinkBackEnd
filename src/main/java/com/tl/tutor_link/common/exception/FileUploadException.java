package com.tl.tutor_link.common.exception;

import org.springframework.http.HttpStatus;

public class FileUploadException extends ApiException {
    public FileUploadException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
