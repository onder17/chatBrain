package com.brainapp.chatbrain.exception;

import com.brainapp.chatbrain.dto.common.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChatBrainException.class)
    public ResponseEntity<ApiError> handleDomainException(ChatBrainException ex, HttpServletRequest req) {
        ApiError err = new ApiError(
                Instant.now(),
                ex.getStatus().value(),
                ex.getCode(),
                ex.getMessage(),
                req.getRequestURI(),
                UUID.randomUUID().toString().substring(0, 8),
                List.of()
        );
        return ResponseEntity.status(ex.getStatus()).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ApiError.FieldViolation(f.getField(), f.getDefaultMessage()))
                .toList();

        ApiError err = new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Gönderilen veriler geçersiz.",
                req.getRequestURI(),
                UUID.randomUUID().toString().substring(0, 8),
                violations
        );
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class, jakarta.validation.ConstraintViolationException.class})
    public ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest req) {
        ApiError err = new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "INVALID_REQUEST",
                "The request parameters or body are invalid.", req.getRequestURI(),
                UUID.randomUUID().toString().substring(0, 8), List.of());
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleStatusException(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        ApiError err = new ApiError(Instant.now(), status.value(), status.name(),
                status == HttpStatus.TOO_MANY_REQUESTS ? "Too many chat requests. Try again shortly." : "Request could not be completed.",
                req.getRequestURI(), UUID.randomUUID().toString().substring(0, 8), List.of());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest req) {
        org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class)
                .error("Unhandled request error for {}", req.getRequestURI(), ex);
        ApiError err = new ApiError(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "Beklenmeyen bir hata oluştu.",
                req.getRequestURI(),
                UUID.randomUUID().toString().substring(0, 8),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
