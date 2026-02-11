package com.moneylegal.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;

@ResponseStatus(HttpStatus.CONFLICT)
@Slf4j
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {
        log.warn("Conflict: {} - path={}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(409)
                        .error("CONFLICT")
                        .message(ex.getMessage())
                        .code("CONFLICT")
                        .path(request.getRequestURI())
                        .build()
        );
    }
}
