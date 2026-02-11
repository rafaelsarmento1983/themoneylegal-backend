package com.moneylegal.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(
            UnauthorizedException ex,
            HttpServletRequest request
    ) {
        // log como warn (é erro esperado)
        log.warn("Unauthorized: {} - path={}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(401)
                        .error("UNAUTHORIZED")
                        .message(ex.getMessage())
                        .code("AUTH_UNAUTHORIZED")
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        log.warn("Bad request: {} - path={}", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(400)
                        .error("BAD_REQUEST")
                        .message(ex.getMessage())
                        .code("BAD_REQUEST")
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(400)
                        .error("VALIDATION_ERROR")
                        .message("Ooops! Dados inválidos. Verifique os campos e tente novamente.")
                        .code("VALIDATION_ERROR")
                        .fieldErrors(fieldErrors)
                        .path(request.getRequestURI())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        // aqui sim é erro inesperado
        log.error("Unexpected error - path={}", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiErrorResponse.builder()
                        .timestamp(Instant.now())
                        .status(500)
                        .error("INTERNAL_SERVER_ERROR")
                        .message("Ooops! Um erro inesperado ocorreu. Por favor, tente novamente.")
                        .code("INTERNAL_ERROR")
                        .path(request.getRequestURI())
                        .build()
        );
    }
}
