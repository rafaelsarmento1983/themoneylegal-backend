package com.moneylegal.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:900000}") long accessTokenExpiration
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = 7 * 24 * 60 * 60 * 1000L;
    }

    public String generateAccessToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        String token = Jwts.builder()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();

        log.info("[JWT] AccessToken gerado para userId={}", userId);
        log.info("[JWT] Token={}", token);
        log.info("[JWT] Expira em={}", expiryDate);

        return token;
    }


    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    // ✅ NOVO: valida e lança exceção tipada (expired vs invalid signature etc)
    public void validateOrThrow(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtAuthenticationException(JwtErrorType.EMPTY, "Token ausente.", null);
        }

        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            throw new JwtAuthenticationException(JwtErrorType.EXPIRED, "Token expirado.", e);

        } catch (SecurityException | SignatureException e) {
            log.warn("JWT invalid signature: {}", e.getMessage());
            throw new JwtAuthenticationException(JwtErrorType.INVALID_SIGNATURE, "Assinatura inválida.", e);

        } catch (MalformedJwtException e) {
            log.warn("JWT malformed: {}", e.getMessage());
            throw new JwtAuthenticationException(JwtErrorType.MALFORMED, "Token malformado.", e);

        } catch (UnsupportedJwtException e) {
            log.warn("JWT unsupported: {}", e.getMessage());
            throw new JwtAuthenticationException(JwtErrorType.UNSUPPORTED, "Token não suportado.", e);

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT invalid: {}", e.getMessage());
            throw new JwtAuthenticationException(JwtErrorType.UNKNOWN, "Token inválido.", e);
        }
    }

    // ⬇️ Mantido: compatibilidade com código existente
    public boolean validateToken(String token) {
        try {
            validateOrThrow(token);
            return true;
        } catch (JwtAuthenticationException e) {
            // já logado em validateOrThrow
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    // Mantido: útil em outros pontos
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date exp = claims.getExpiration();
            return exp != null && exp.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}
