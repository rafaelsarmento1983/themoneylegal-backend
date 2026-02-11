package com.moneylegal.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

/**
 * Entidade RefreshToken - Token para renovar access token
 * 
 * Fluxo:
 * 1. Login retorna: accessToken (15min) + refreshToken (7 dias)
 * 2. AccessToken expira -> Cliente usa refreshToken
 * 3. RefreshToken gera novo par de tokens
 * 4. RefreshToken tem vida útil de 7 dias
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_tokens_user", columnList = "user_id"),
    @Index(name = "idx_refresh_tokens_token", columnList = "token", unique = true),
    @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private Boolean revoked = false;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    /**
     * Business methods
     */
    public boolean isValid() {
        return !revoked && expiresAt.isAfter(LocalDateTime.now());
    }

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }

    public void revoke() {
        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
    }

    /**
     * Factory method
     */
    public static RefreshToken create(String userId, String token, int expirationDays) {
        return RefreshToken.builder()
            .userId(userId)
            .token(token)
            .expiresAt(LocalDateTime.now().plusDays(expirationDays))
            .build();
    }
}
