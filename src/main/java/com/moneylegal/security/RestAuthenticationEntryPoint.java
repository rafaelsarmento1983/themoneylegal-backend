package com.moneylegal.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneylegal.exception.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        String code = "AUTH_UNAUTHORIZED";
        String message = "Token inválido ou expirado.";

        if (authException instanceof JwtAuthenticationException jwtEx) {
            switch (jwtEx.getType()) {
                case EXPIRED -> {
                    code = "AUTH_TOKEN_EXPIRED";
                    message = "Sua sessão expirou. Faça login novamente.";
                }
                case INVALID_SIGNATURE -> {
                    code = "AUTH_TOKEN_INVALID_SIGNATURE";
                    message = "Token inválido.";
                }
                case MALFORMED -> {
                    code = "AUTH_TOKEN_MALFORMED";
                    message = "Token malformado.";
                }
                case UNSUPPORTED -> {
                    code = "AUTH_TOKEN_UNSUPPORTED";
                    message = "Token não suportado.";
                }
                case EMPTY -> {
                    code = "AUTH_TOKEN_MISSING";
                    message = "Token ausente.";
                }
                default -> {
                    code = "AUTH_TOKEN_INVALID";
                    message = "Token inválido.";
                }
            }
        }

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(401)
                .error("UNAUTHORIZED")
                .message(message)
                .code(code)
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), body);
    }
}
