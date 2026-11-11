package com.brainapp.chatbrain.exception;

import org.springframework.http.HttpStatus;

public class InvalidParameterException extends ChatBrainException {
    public InvalidParameterException(String message) {
        super(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", message);
    }
}
