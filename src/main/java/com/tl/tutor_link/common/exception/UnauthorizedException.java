package com.tl.tutor_link.common.exception;

import com.tl.tutor_link.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}