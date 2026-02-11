package com.moneylegal.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;     // ex: UNAUTHORIZED
    private String message;   // ex: Email ou senha inválidos
    private String path;      // /api/v1/auth/login
    private String code;      // ex: AUTH_INVALID_CREDENTIALS (opcional)
    private Map<String, String> fieldErrors; // validação (opcional)
}
