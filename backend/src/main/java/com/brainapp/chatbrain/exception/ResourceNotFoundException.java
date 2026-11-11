package com.brainapp.chatbrain.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ChatBrainException {
    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", message);
    }
}
