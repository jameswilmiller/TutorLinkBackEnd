package com.tl.tutor_link.common.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final ErrorCode code;

    protected ApiException(HttpStatus status, String message) {
        this(status, message, null);
    }
    protected ApiException(HttpStatus status, String message, ErrorCode code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorCode getCode() {
        return code;
    }
}