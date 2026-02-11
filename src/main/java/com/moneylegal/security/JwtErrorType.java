package com.moneylegal.security;

public enum JwtErrorType {
    EXPIRED,
    INVALID_SIGNATURE,
    MALFORMED,
    UNSUPPORTED,
    EMPTY,
    UNKNOWN
}
