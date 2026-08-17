package com.tl.tutor_link.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(String message, ErrorCode code) {
        super(HttpStatus.BAD_REQUEST, message, code);
    }
}