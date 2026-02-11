package com.moneylegal.security;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

    private final JwtErrorType type;

    public JwtAuthenticationException(JwtErrorType type, String msg, Throwable cause) {
        super(msg, cause);
        this.type = type;
    }

    public JwtErrorType getType() {
        return type;
    }
}
