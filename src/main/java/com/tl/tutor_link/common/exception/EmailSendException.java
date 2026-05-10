package com.tl.tutor_link.common.exception;


import org.springframework.http.HttpStatus;

public class EmailSendException extends ApiException {
    public EmailSendException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}