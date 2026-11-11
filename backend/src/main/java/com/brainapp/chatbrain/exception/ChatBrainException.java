package com.brainapp.chatbrain.exception;

import org.springframework.http.HttpStatus;

public abstract class ChatBrainException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    protected ChatBrainException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
