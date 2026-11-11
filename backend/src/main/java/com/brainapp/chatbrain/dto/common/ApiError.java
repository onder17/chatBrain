package com.brainapp.chatbrain.dto.common;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String code,
        String message,
        String path,
        String traceId,
        List<FieldViolation> fieldErrors
) {
    public record FieldViolation(String field, String message) {}
}
